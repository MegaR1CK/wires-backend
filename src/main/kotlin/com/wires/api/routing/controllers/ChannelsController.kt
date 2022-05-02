package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.getUserId
import com.wires.api.extensions.proceedJsonPart
import com.wires.api.extensions.receivePagingParams
import com.wires.api.extensions.receivePathOrException
import com.wires.api.extensions.respondList
import com.wires.api.extensions.respondObject
import com.wires.api.routing.requestparams.ChannelCreateParams
import com.wires.api.service.ChannelsService
import com.wires.api.websockets.Connection
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.util.*

private const val CHANNELS_PATH = "$API_VERSION/channels"
private const val CHANNEL_GET_PATH = "$CHANNELS_PATH/{id}"
private const val MESSAGES_GET_PATH = "$CHANNEL_GET_PATH/messages"
private const val CHANNEL_LISTEN_PATH = "$CHANNEL_GET_PATH/listen"
private const val CHANNEL_CREATE_PATH = "$CHANNELS_PATH/create"

fun Routing.channelsController() {

    val channelsService: ChannelsService by inject()
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

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
            call.receiveMultipart().forEachPart { part ->
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

        /** Прослушивание канала по вебсокетам */
        webSocket(CHANNEL_LISTEN_PATH) {
            val thisConnection = Connection(this)
            connections += thisConnection
            val channelId = call.receivePathOrException("id") { it.toInt() }
            channelsService.listenChannel(call.getUserId(), channelId, incoming, connections, thisConnection)
        }
    }
}
