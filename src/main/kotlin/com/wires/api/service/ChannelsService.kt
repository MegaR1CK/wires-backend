package com.wires.api.service

import com.wires.api.database.params.ChannelInsertParams
import com.wires.api.database.params.ImageInsertParams
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.mappers.ChannelsMapper
import com.wires.api.model.ChannelType
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.ImagesRepository
import com.wires.api.repository.MessagesRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.ForbiddenException
import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.NotFoundException
import com.wires.api.routing.PersonalChannelExistsException
import com.wires.api.routing.SocketException
import com.wires.api.routing.StorageException
import com.wires.api.routing.requestparams.ChannelCreateParams
import com.wires.api.routing.requestparams.MessageSendParams
import com.wires.api.routing.respondmodels.ChannelPreviewResponse
import com.wires.api.routing.respondmodels.ChannelResponse
import com.wires.api.routing.respondmodels.MessageResponse
import com.wires.api.routing.respondmodels.ObjectResponse
import com.wires.api.websockets.Connection
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ChannelsService : KoinComponent {

    companion object {
        private const val CHANNEL_IMAGE_DEFAULT_PATTERN = "https://ui-avatars.com/api/?background=random&name="
        private const val CHANNEL_IMAGE_DEFAULT_SIZE = 64
    }

    private val userRepository: UserRepository by inject()
    private val channelsRepository: ChannelsRepository by inject()
    private val messagesRepository: MessagesRepository by inject()
    private val storageRepository: StorageRepository by inject()
    private val imagesRepository: ImagesRepository by inject()
    private val channelsMapper: ChannelsMapper by inject()

    suspend fun getUserChannels(userId: Int): List<ChannelPreviewResponse> {
        val user = userRepository.findUserById(userId) ?: throw NotFoundException()
        val channels = channelsRepository.getUserChannels(user.id)
        channels.map { preview ->
            preview.lastMessage = messagesRepository.getMessages(user.id, preview.id, 1, 0).firstOrNull()
            preview.unreadMessages = messagesRepository.getUnreadMessagesCount(userId, preview.id)
        }
        return channels.sortedByDescending { it.lastMessage?.sendTime }.map(channelsMapper::fromModelToResponse)
    }

    suspend fun getChannel(userId: Int, channelId: Int): ChannelResponse {
        val channel = channelsRepository.getChannel(channelId) ?: throw NotFoundException()
        if (!channel.containsUser(userId)) throw ForbiddenException()
        return channelsMapper.fromModelToResponse(channel)
    }

    suspend fun createChannel(
        userId: Int,
        channelCreateParams: ChannelCreateParams?,
        imageBytes: ByteArray?
    ): ChannelResponse {
        if (channelCreateParams == null) throw MissingArgumentsException()
        if (channelCreateParams.type == ChannelType.PERSONAL) checkPersonalChannelExisting(userId, channelCreateParams)
        val imageUrl = when {
            imageBytes != null -> getChannelImageUrl(imageBytes)
            channelCreateParams.name != null -> getChannelPlaceholderUrl(channelCreateParams.name)
            else -> null
        }
        val insertParams = ChannelInsertParams(
            name = channelCreateParams.name,
            type = channelCreateParams.type.name,
            imageUrl = imageUrl
        )
        val createdChannelId = channelsRepository.createChannel(insertParams)
        fillChannel(
            channelId = createdChannelId,
            membersIds = channelCreateParams.membersIds + userId
        )
        return channelsMapper.fromModelToResponse(
            channel = channelsRepository.getChannel(createdChannelId) ?: throw NotFoundException()
        )
    }

    suspend fun getChannelMessages(userId: Int, channelId: Int, limit: Int, offset: Long): List<MessageResponse> {
        val channel = channelsRepository.getChannel(channelId) ?: throw NotFoundException()
        if (!channel.containsUser(userId)) throw ForbiddenException()
        return messagesRepository
            .getMessages(userId, channelId, limit, offset)
            .map(channelsMapper::fromModelToResponse)
    }

    suspend fun readChannelMessages(userId: Int, channelId: Int, messagesIds: List<Int>) {
        val channel = channelsRepository.getChannel(channelId) ?: throw NotFoundException()
        if (!channel.containsUser(userId)) throw ForbiddenException()
        messagesRepository.readMessages(userId, messagesIds)
    }

    suspend fun listenChannel(
        userId: Int,
        channelId: Int,
        incomingFlow: ReceiveChannel<Frame>,
        connections: MutableSet<Connection>,
        thisConnection: Connection,
        onConnectionClose: () -> Unit
    ) {
        channelsRepository.getChannel(channelId)?.let { channel ->
            if (channel.containsUser(userId)) {
                try {
                    incomingFlow.consumeAsFlow()
                        .mapNotNull { it as? Frame.Text }
                        .map { it.readText() }
                        .map { Json.decodeFromString<MessageSendParams>(it) }
                        .collect { receivedMessage ->
                            val messageId = messagesRepository.addMessage(
                                MessageInsertParams(
                                    authorId = userId,
                                    channelId = channelId,
                                    text = receivedMessage.text,
                                    isInitial = receivedMessage.isInitial
                                )
                            )
                            messagesRepository.getMessageById(messageId)?.let { message ->
                                connections.forEach { connection ->
                                    connection.session.sendSerializedBase(
                                        ObjectResponse(channelsMapper.fromModelToResponse(message)),
                                        KotlinxWebsocketSerializationConverter(Json),
                                        Charsets.UTF_8
                                    )
                                }
                            } ?: throw UnknownError()
                        }
                } catch (throwable: Throwable) {
                    throw if (throwable.message != null) {
                        SocketException(throwable.message.orEmpty())
                    } else {
                        SocketException()
                    }
                } finally {
                    connections -= thisConnection
                    onConnectionClose()
                }
            } else {
                throw ForbiddenException()
            }
        } ?: run {
            throw NotFoundException()
        }
    }

    /**
     * Проверка на существование личного канала
     */
    private suspend fun checkPersonalChannelExisting(userId: Int, params: ChannelCreateParams) {
        val channelMember = channelsRepository
            .getUserChannels(userId)
            .find { it.type == ChannelType.PERSONAL && it.dialogMember?.id == params.membersIds.firstOrNull() }
        if (channelMember != null) throw PersonalChannelExistsException()
    }

    private suspend fun getChannelImageUrl(bytes: ByteArray): EntityID<String> {
        val image = storageRepository.uploadFile(bytes) ?: throw StorageException()
        return imagesRepository.addImage(ImageInsertParams(image.url, image.size.width, image.size.height))
    }

    private suspend fun getChannelPlaceholderUrl(channelName: String) = imagesRepository.addImage(
        ImageInsertParams(
            url = CHANNEL_IMAGE_DEFAULT_PATTERN +
                channelName.replace(' ', '+') +
                System.currentTimeMillis(),
            width = CHANNEL_IMAGE_DEFAULT_SIZE,
            height = CHANNEL_IMAGE_DEFAULT_SIZE
        )
    )

    private suspend fun fillChannel(channelId: Int, membersIds: List<Int>) {
        membersIds.forEach { memberId ->
            channelsRepository.addUserToChannel(memberId, channelId)
        }
    }
}
