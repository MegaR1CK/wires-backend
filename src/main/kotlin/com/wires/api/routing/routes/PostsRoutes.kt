package com.wires.api.routing.routes

import com.wires.api.database.params.PostInsertParams
import com.wires.api.extensions.handleRouteWithAuth
import com.wires.api.extensions.handleRouteWithPathParams
import com.wires.api.extensions.receiveBodyParams
import com.wires.api.extensions.receiveIntPathParameter
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.PostCreateParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

private const val POSTS_PATH = "$API_VERSION/posts"
private const val POST_CREATE_PATH = "$POSTS_PATH/create"
private const val POST_GET_PATH = "$POSTS_PATH/{id}"

fun Application.registerPostsRoutes(
    userRepository: UserRepository,
    postsRepository: PostsRepository
) = routing {
    getPostsCompilation(userRepository, postsRepository)
    createPost(postsRepository)
    getPost(userRepository, postsRepository)
}

fun Route.getPostsCompilation(
    userRepository: UserRepository,
    postsRepository: PostsRepository
) = handleRouteWithAuth(POSTS_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        val topic = call.request.queryParameters["topic"]
        if (topic == null) {
            val currentUser = userRepository.findUserById(userId)
            currentUser?.let { user ->
                call.respond(
                    HttpStatusCode.OK,
                    postsRepository.getPostsList(user.interests)
                        .map { it.toResponse(userRepository.findUserById(it.userId)?.toResponse()) }
                )
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        } else {
            call.respond(
                HttpStatusCode.OK,
                postsRepository.getPostsList(listOf(topic)).map { post ->
                    post.toResponse(userRepository.findUserById(post.userId)?.toResponse())
                }
            )
        }
    }
}

fun Route.createPost(
    postsRepository: PostsRepository
) = handleRouteWithAuth(POST_CREATE_PATH, HttpMethod.Post) { scope, call, userId ->
    scope.launch {
        call.receiveBodyParams<PostCreateParams> { params ->
            val insertParams = PostInsertParams(
                text = params.text,
                imageUrl = params.imageUrl,
                topic = params.topic,
                userId = userId
            )
            postsRepository.createPost(insertParams)
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getPost(
    userRepository: UserRepository,
    postsRepository: PostsRepository
) = handleRouteWithPathParams(POST_GET_PATH, HttpMethod.Get) { scope, call, params ->
    scope.launch {
        call.receiveIntPathParameter("id") { postId ->
            val currentPost = postsRepository.getPost(postId)
            currentPost?.let {
                call.respond(
                    HttpStatusCode.OK,
                    currentPost.toResponse(userRepository.findUserById(currentPost.userId)?.toResponse())
                )
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Post not found")
            }
        }
    }
}
