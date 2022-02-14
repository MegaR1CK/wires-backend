package com.wires.api.routing.routes

import com.wires.api.extensions.handleRouteWithAuth
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

private const val POSTS_PATH = "$API_VERSION/posts"

fun Application.registerPostsRoutes(
    userRepository: UserRepository,
    postsRepository: PostsRepository
) = routing {
    getPostsCompilation(userRepository, postsRepository)
}

fun Route.getPostsCompilation(
    userRepository: UserRepository,
    postsRepository: PostsRepository
) = handleRouteWithAuth(POSTS_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        val topic = call.request.queryParameters["topic"]
        if (topic == null) {
            val currentUser = userRepository.findUserById(userId)
            currentUser?.let {
                call.respond(
                    HttpStatusCode.OK,
                    postsRepository.getPostsList(currentUser.interests)
                        .map { it.toResponse(it.likedUserIds.contains(userId)) }
                )
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        } else {
            call.respond(
                HttpStatusCode.OK,
                postsRepository.getPostsList(listOf(topic)).map { it.toResponse(it.likedUserIds.contains(userId)) }
            )
        }
    }
}
