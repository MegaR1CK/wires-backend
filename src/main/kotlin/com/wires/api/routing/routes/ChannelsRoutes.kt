package com.wires.api.routing.routes

import com.google.gson.GsonBuilder
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.extensions.handleRouteWithAuth
import com.wires.api.extensions.receiveBodyParams
import com.wires.api.extensions.receiveIntPathParameter
import com.wires.api.extensions.userId
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.MessagesRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.MessageSendParams
import com.wires.api.websockets.Connection
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.util.*
import kotlin.run

private const val CHANNELS_PATH = "$API_VERSION/channels"
private const val GET_CHANNEL_PATH = "$CHANNELS_PATH/{id}"
private const val GET_CHANNEL_MESSAGES_PATH = "$GET_CHANNEL_PATH/messages"
private const val SEND_MESSAGE_PATH = "$GET_CHANNEL_PATH/send"
private const val LISTEN_CHANNEL_PATH = "$GET_CHANNEL_PATH/listen"

fun Application.registerChannelsRoutes(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository,
    messagesRepository: MessagesRepository
) = routing {
    getUserChannels(userRepository, channelsRepository)
    getChannel(userRepository, channelsRepository)
    getChannelMessages(userRepository, channelsRepository, messagesRepository)
    sendMessage(channelsRepository, messagesRepository)
    listenChannel(userRepository, channelsRepository, messagesRepository)
}

fun Route.getUserChannels(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository
) = handleRouteWithAuth(CHANNELS_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        userRepository.findUserById(userId)?.let { user ->
            call.respond(
                HttpStatusCode.OK,
                channelsRepository.getUserChannels(user.channels).map { it.toPreviewResponse() }
            )
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.getChannel(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository
) = handleRouteWithAuth(GET_CHANNEL_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        val channelId = call.receiveIntPathParameter("id") ?: return@launch
        channelsRepository.getChannel(channelId)?.let { channel ->
            if (channel.membersIds.contains(userId)) {
                call.respond(
                    HttpStatusCode.OK,
                    channel.toResponse(userRepository.getUsersList(channel.membersIds).map { it.toPreviewResponse() })
                )
            } else {
                call.respond(HttpStatusCode.Forbidden, "User is not member of channel")
            }
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Channel not found")
        }
    }
}

fun Route.getChannelMessages(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository,
    messagesRepository: MessagesRepository
) = handleRouteWithAuth(GET_CHANNEL_MESSAGES_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        val channelId = call.receiveIntPathParameter("id") ?: return@launch
        channelsRepository.getChannel(channelId)?.let { channel ->
            if (channel.membersIds.contains(userId)) {
                call.respond(
                    HttpStatusCode.OK,
                    messagesRepository.getMessages(channelId)
                        .map { it.toResponse(userRepository.findUserById(it.userId)?.toPreviewResponse()) }
                )
            } else {
                call.respond(HttpStatusCode.Forbidden, "User is not member of channel")
            }
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Channel not found")
        }
    }
}

fun Route.sendMessage(
    channelsRepository: ChannelsRepository,
    messagesRepository: MessagesRepository
) = handleRouteWithAuth(SEND_MESSAGE_PATH, HttpMethod.Post) { scope, call, userId ->
    scope.launch {
        val channelId = call.receiveIntPathParameter("id") ?: return@launch
        val messageParams = call.receiveBodyParams<MessageSendParams>() ?: return@launch
        channelsRepository.getChannel(channelId)?.let { channel ->
            if (channel.membersIds.contains(userId)) {
                messagesRepository.addMessage(
                    MessageInsertParams(
                        authorId = userId,
                        channelId = channel.id,
                        text = messageParams.text
                    )
                )
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Forbidden, "User is not member of channel")
            }
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Channel not found")
        }
    }
}

fun Route.listenChannel(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository,
    messagesRepository: MessagesRepository
) {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    authenticate("jwt") {
        webSocket(LISTEN_CHANNEL_PATH) {
            connections += Connection(this)
            val channelId = call.receiveIntPathParameter("id") ?: return@webSocket
            call.userId?.let { id ->
                channelsRepository.getChannel(channelId)?.let { channel ->
                    if (channel.membersIds.contains(id)) {
                        incoming.consumeAsFlow()
                            .mapNotNull { it as? Frame.Text }
                            .map { it.readText() }
                            .map { GsonBuilder().create().fromJson(it, MessageSendParams::class.java) }
                            .collect { receivedMessage ->
                                val messageId = messagesRepository.addMessage(
                                    MessageInsertParams(
                                        authorId = id,
                                        channelId = channelId,
                                        text = receivedMessage.text
                                    )
                                )
                                messagesRepository.getMessageById(messageId)?.let { message ->
                                    val userResponse = userRepository.findUserById(message.userId)?.toPreviewResponse()
                                    connections?.forEach { connection ->
                                        connection.session.sendSerializedBase(
                                            message.toResponse(userResponse),
                                            GsonWebsocketContentConverter(),
                                            Charsets.UTF_8
                                        )
                                    }
                                } ?: run {
                                    return@collect call.respond(
                                        status = HttpStatusCode.BadRequest,
                                        message = "Problems while sending message"
                                    )
                                }
                            }
                    } else {
                        return@webSocket call.respond(HttpStatusCode.Forbidden, "User is not member of channel")
                    }
                } ?: run {
                    return@webSocket call.respond(HttpStatusCode.NotFound, "Channel not found")
                }
            } ?: run {
                return@webSocket call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
