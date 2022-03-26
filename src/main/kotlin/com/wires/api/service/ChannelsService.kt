package com.wires.api.service

import com.google.gson.Gson
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.MessagesRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.ForbiddenException
import com.wires.api.routing.NotFoundException
import com.wires.api.routing.requestparams.MessageSendParams
import com.wires.api.routing.respondmodels.ChannelPreviewResponse
import com.wires.api.routing.respondmodels.ChannelResponse
import com.wires.api.routing.respondmodels.MessageResponse
import com.wires.api.utils.DateFormatter
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

    private val userRepository: UserRepository by inject()
    private val channelsRepository: ChannelsRepository by inject()
    private val messagesRepository: MessagesRepository by inject()
    private val dateFormatter: DateFormatter by inject()
    private val gson: Gson by inject()

    suspend fun getUserChannels(userId: Int): List<ChannelPreviewResponse> {
        userRepository.findUserById(userId)?.let { user ->
            return channelsRepository.getUserChannels(user.channels).map { it.toPreviewResponse() }
        } ?: throw NotFoundException()
    }

    suspend fun getChannel(userId: Int, channelId: Int): ChannelResponse {
        channelsRepository.getChannel(channelId)?.let { channel ->
            return if (channel.membersIds.contains(userId)) {
                channel.toResponse(userRepository.getUsersList(channel.membersIds).map { it.toPreviewResponse() })
            } else {
                throw ForbiddenException()
            }
        } ?: throw NotFoundException()
    }

    suspend fun getChannelMessages(userId: Int, channelId: Int): List<MessageResponse> {
        channelsRepository.getChannel(channelId)?.let { channel ->
            return if (channel.membersIds.contains(userId)) {
                messagesRepository.getMessages(channelId)
                    .map { message ->
                        message.toResponse(
                            userRepository.findUserById(message.userId)?.toPreviewResponse(),
                            dateFormatter.dateTimeToFullString(message.sendTime)
                        )
                    }
            } else {
                throw ForbiddenException()
            }
        } ?: throw NotFoundException()
    }

    suspend fun listenChannel(
        userId: Int,
        channelId: Int,
        incomingFlow: ReceiveChannel<Frame>,
        connections: Set<Connection>
    ) {
        channelsRepository.getChannel(channelId)?.let { channel ->
            if (channel.membersIds.contains(userId)) {
                incomingFlow.consumeAsFlow()
                    .mapNotNull { it as? Frame.Text }
                    .map { it.readText() }
                    .map { gson.fromJson(it, MessageSendParams::class.java) }
                    .collect { receivedMessage ->
                        val messageId = messagesRepository.addMessage(
                            MessageInsertParams(
                                authorId = userId,
                                channelId = channelId,
                                text = receivedMessage.text
                            )
                        )
                        messagesRepository.getMessageById(messageId)?.let { message ->
                            val userResponse = userRepository.findUserById(message.userId)?.toPreviewResponse()
                            connections.forEach { connection ->
                                connection.session.sendSerializedBase(
                                    message.toResponse(
                                        userResponse,
                                        dateFormatter.dateTimeToFullString(message.sendTime)
                                    ),
                                    GsonWebsocketContentConverter(),
                                    Charsets.UTF_8
                                )
                            }
                        } ?: throw UnknownError()
                    }
            } else {
                throw ForbiddenException()
            }
        } ?: run {
            throw NotFoundException()
        }
    }
}