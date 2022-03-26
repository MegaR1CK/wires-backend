package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.getUserId
import com.wires.api.extensions.receivePathOrException
import com.wires.api.service.ChannelsService
import com.wires.api.websockets.Connection
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.util.*

private const val CHANNELS_PATH = "$API_VERSION/channels"
private const val CHANNEL_GET_PATH = "$CHANNELS_PATH/{id}"
private const val MESSAGES_GET_PATH = "$CHANNEL_GET_PATH/messages"
private const val CHANNEL_LISTEN_PATH = "$CHANNEL_GET_PATH/listen"

fun Routing.channelsModule() {

    val channelsService: ChannelsService by inject()
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    authenticate("jwt") {
        get(CHANNELS_PATH) {
            call.respond(HttpStatusCode.OK, channelsService.getUserChannels(call.getUserId()))
        }

        get(CHANNEL_GET_PATH) {
            val channelId = call.receivePathOrException("id") { it.toInt() }
            call.respond(HttpStatusCode.OK, channelsService.getChannel(call.getUserId(), channelId))
        }

        get(MESSAGES_GET_PATH) {
            val channelId = call.receivePathOrException("id") { it.toInt() }
            call.respond(HttpStatusCode.OK, channelsService.getChannelMessages(call.getUserId(), channelId))
        }

        webSocket(CHANNEL_LISTEN_PATH) {
            connections += Connection(this)
            val channelId = call.receivePathOrException("id") { it.toInt() }
            channelsService.listenChannel(call.getUserId(), channelId, incoming, connections)
        }
    }
}
