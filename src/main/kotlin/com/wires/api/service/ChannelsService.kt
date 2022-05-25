package com.wires.api.service

import com.google.gson.Gson
import com.wires.api.database.params.ChannelInsertParams
import com.wires.api.database.params.ImageInsertParams
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.mappers.ChannelsMapper
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.ImagesRepository
import com.wires.api.repository.MessagesRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.ForbiddenException
import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.NotFoundException
import com.wires.api.routing.SocketException
import com.wires.api.routing.StorageException
import com.wires.api.routing.requestparams.ChannelCreateParams
import com.wires.api.routing.requestparams.MessageSendParams
import com.wires.api.routing.respondmodels.ChannelPreviewResponse
import com.wires.api.routing.respondmodels.ChannelResponse
import com.wires.api.routing.respondmodels.MessageResponse
import com.wires.api.routing.respondmodels.ObjectResponse
import com.wires.api.websockets.Connection
import io.ktor.serialization.gson.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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
    private val gson: Gson by inject()

    suspend fun getUserChannels(userId: Int): List<ChannelPreviewResponse> {
        userRepository.findUserById(userId)?.let { user ->
            val channels = channelsRepository.getUserChannels(user.id)
            channels.map {
                it.lastMessage = messagesRepository.getMessages(user.id, it.id, 1, 0).firstOrNull()
                it.unreadMessages = messagesRepository.getUnreadMessagesCount(userId, it.id)
            }
            return channels.sortedByDescending { it.lastMessage?.sendTime }.map(channelsMapper::fromModelToResponse)
        } ?: throw NotFoundException()
    }

    suspend fun getChannel(userId: Int, channelId: Int): ChannelResponse {
        channelsRepository.getChannel(channelId)?.let { channel ->
            return if (channel.containsUser(userId)) {
                channelsMapper.fromModelToResponse(channel)
            } else {
                throw ForbiddenException()
            }
        } ?: throw NotFoundException()
    }

    suspend fun createChannel(
        userId: Int,
        channelCreateParams: ChannelCreateParams?,
        imageBytes: ByteArray?
    ): ChannelResponse {
        channelCreateParams?.let { params ->
            val imageUrl = imageBytes?.let { bytes ->
                val image = storageRepository.uploadFile(bytes) ?: throw StorageException()
                imagesRepository.addImage(ImageInsertParams(image.url, image.size.width, image.size.height))
            } ?: run {
                imagesRepository.addImage(
                    ImageInsertParams(
                        url = CHANNEL_IMAGE_DEFAULT_PATTERN +
                            params.name.replace(' ', '+') +
                            System.currentTimeMillis(),
                        width = CHANNEL_IMAGE_DEFAULT_SIZE,
                        height = CHANNEL_IMAGE_DEFAULT_SIZE
                    )
                )
            }
            val insertParams = ChannelInsertParams(
                name = params.name,
                type = params.type,
                imageUrl = imageUrl
            )
            val createdChannelId = channelsRepository.createChannel(insertParams)
            channelsRepository.addUserToChannel(userId, createdChannelId)
            channelCreateParams.membersIds.forEach { memberId ->
                channelsRepository.addUserToChannel(memberId, createdChannelId)
            }
            return channelsMapper.fromModelToResponse(
                channel = channelsRepository.getChannel(createdChannelId) ?: throw NotFoundException()
            )
        } ?: throw MissingArgumentsException()
    }

    suspend fun getChannelMessages(userId: Int, channelId: Int, limit: Int, offset: Long): List<MessageResponse> {
        channelsRepository.getChannel(channelId)?.let { channel ->
            return if (channel.containsUser(userId)) {
                messagesRepository
                    .getMessages(userId, channelId, limit, offset)
                    .map(channelsMapper::fromModelToResponse)
            } else {
                throw ForbiddenException()
            }
        } ?: throw NotFoundException()
    }

    suspend fun readChannelMessages(userId: Int, channelId: Int, messagesIds: List<Int>) {
        channelsRepository.getChannel(channelId)?.let { channel ->
            if (channel.containsUser(userId)) {
                messagesRepository.readMessages(userId, messagesIds)
            } else {
                throw ForbiddenException()
            }
        } ?: throw NotFoundException()
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
                        .map { gson.fromJson(it, MessageSendParams::class.java) }
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
                                        GsonWebsocketContentConverter(),
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
}
