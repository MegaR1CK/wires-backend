package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.getUserId
import com.wires.api.extensions.proceedJsonPart
import com.wires.api.extensions.receiveBodyOrException
import com.wires.api.extensions.receiveMultipartOrException
import com.wires.api.extensions.receivePagingParams
import com.wires.api.extensions.receivePathOrException
import com.wires.api.extensions.respondEmpty
import com.wires.api.extensions.respondList
import com.wires.api.extensions.respondObject
import com.wires.api.routing.requestparams.ChannelCreateParams
import com.wires.api.routing.requestparams.MessagesReadParams
import com.wires.api.service.ChannelsService
import com.wires.api.websockets.Connection
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.util.*

private const val CHANNELS_PATH = "$API_VERSION/channels"
private const val CHANNEL_GET_PATH = "$CHANNELS_PATH/{id}"
private const val MESSAGES_GET_PATH = "$CHANNEL_GET_PATH/messages"
private const val CHANNEL_LISTEN_PATH = "$CHANNEL_GET_PATH/listen"
private const val CHANNEL_CREATE_PATH = "$CHANNELS_PATH/create"
private const val CHANNEL_READ_PATH = "$CHANNEL_GET_PATH/read"

fun Routing.channelsController() {

    val channelsService: ChannelsService by inject()
    val connectionsMap = mutableMapOf<Int, MutableSet<Connection>>()

    authenticate("jwt") {

        /** Получение каналов пользователя */
        get(CHANNELS_PATH) {
            call.respondList(HttpStatusCode.OK, channelsService.getUserChannels(call.getUserId()))
        }

        /** Получение информации о канале */
        get(CHANNEL_GET_PATH) {
            val channelId = call.receivePathOrException("id") { it.toInt() }
            call.respondObject(HttpStatusCode.OK, channelsService.getChannel(call.getUserId(), channelId))
        }

        /** Создание нового канала */
        post(CHANNEL_CREATE_PATH) {
            var receivedChannelParams: ChannelCreateParams? = null
            var receivedImageBytes: ByteArray? = null
            call.receiveMultipartOrException().forEachPart { part ->
                when (part) {
                    is PartData.FormItem ->
                        if (part.name == "create_params") {
                            receivedChannelParams = part.proceedJsonPart<ChannelCreateParams>()
                        }
                    is PartData.FileItem ->
                        if (part.name == "image") receivedImageBytes = part.streamProvider().readBytes()
                    else -> { }
                }
            }
            call.respondObject(
                code = HttpStatusCode.Created,
                response = channelsService.createChannel(call.getUserId(), receivedChannelParams, receivedImageBytes)
            )
        }

        /** Получение сообщений в канале */
        get(MESSAGES_GET_PATH) {
            val channelId = call.receivePathOrException("id") { it.toInt() }
            val pagingParams = call.receivePagingParams()
            call.respondList(
                code = HttpStatusCode.OK,
                response = channelsService.getChannelMessages(
                    userId = call.getUserId(),
                    channelId = channelId,
                    limit = pagingParams.limit,
                    offset = pagingParams.offset
                )
            )
        }

        post(CHANNEL_READ_PATH) {
            val channelId = call.receivePathOrException("id") { it.toInt() }
            val params = call.receiveBodyOrException<MessagesReadParams>()
            channelsService.readChannelMessages(call.getUserId(), channelId, params.messagesIds)
            call.respondEmpty(HttpStatusCode.OK)
        }

        /** Прослушивание канала по вебсокетам */
        webSocket(CHANNEL_LISTEN_PATH) {
            val thisConnection = Connection(this)
            val channelId = call.receivePathOrException("id") { it.toInt() }
            if (connectionsMap.contains(channelId)) {
                connectionsMap[channelId]?.add(thisConnection)
            } else {
                connectionsMap[channelId] = Collections.synchronizedSet(mutableSetOf(thisConnection))
            }
            call.application.environment.log.info(
                "WEBSOCKET: new user connected to channel $channelId. " +
                    "Listening users: ${connectionsMap[channelId]?.size}"
            )
            connectionsMap[channelId]?.let { connectionsSet ->
                channelsService.listenChannel(
                    call.getUserId(),
                    channelId,
                    incoming,
                    connectionsSet,
                    thisConnection
                ) {
                    call.application.environment.log.info(
                        "WEBSOCKET: user disconnected from channel $channelId. " +
                            "Listening users: ${connectionsMap[channelId]?.size}"
                    )
                    if (connectionsMap[channelId]?.isEmpty() == true) connectionsMap.remove(channelId)
                }
            }
        }
    }
}
