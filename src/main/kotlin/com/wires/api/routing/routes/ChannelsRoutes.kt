package com.wires.api.routing.routes

import com.wires.api.extensions.handleRouteWithAuth
import com.wires.api.extensions.receiveIntPathParameter
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

private const val CHANNELS_PATH = "$API_VERSION/channels"
private const val GET_CHANNEL_PATH = "$CHANNELS_PATH/{id}"

fun Application.registerChannelsRoutes(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository
) = routing {
    getUserChannels(userRepository, channelsRepository)
    getChannel(userRepository, channelsRepository)
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
