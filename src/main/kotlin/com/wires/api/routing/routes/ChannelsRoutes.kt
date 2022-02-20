package com.wires.api.routing.routes

import com.wires.api.extensions.handleRouteWithAuth
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

private const val CHANNELS_PATH = "$API_VERSION/channels"

fun Application.registerChannelsRoutes(
    userRepository: UserRepository,
    channelsRepository: ChannelsRepository
) = routing {
    getUserChannels(userRepository, channelsRepository)
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
