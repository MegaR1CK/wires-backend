package com.wires.api.routing.routes

import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.params.PostInsertParams
import com.wires.api.extensions.*
import com.wires.api.repository.CommentsRepository
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.PostCommentParams
import com.wires.api.routing.requestparams.PostCreateParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

private const val POSTS_PATH = "$API_VERSION/posts"
private const val POST_CREATE_PATH = "$POSTS_PATH/create"
private const val POST_GET_PATH = "$POSTS_PATH/{id}"
private const val POST_LIKE_PATH = "$POST_GET_PATH/like"
private const val POST_COMMENT_PATH = "$POST_GET_PATH/comment"

fun Application.registerPostsRoutes(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    commentsRepository: CommentsRepository
) = routing {
    getPostsCompilation(userRepository, postsRepository)
    createPost(postsRepository)
    getPost(userRepository, postsRepository)
    likePost(postsRepository)
    commentPost(postsRepository, commentsRepository)
    getPostComments(userRepository, postsRepository, commentsRepository)
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
                        .map { it.toResponse(userRepository.findUserById(it.userId)?.toPreviewResponse()) }
                )
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        } else {
            call.respond(
                HttpStatusCode.OK,
                postsRepository.getPostsList(listOf(topic)).map { post ->
                    post.toResponse(userRepository.findUserById(post.userId)?.toPreviewResponse())
                }
            )
        }
    }
}

fun Route.createPost(
    postsRepository: PostsRepository
) = handleRouteWithAuth(POST_CREATE_PATH, HttpMethod.Post) { scope, call, userId ->
    scope.launch {
        val params = call.receiveBodyParams<PostCreateParams>() ?: return@launch
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

fun Route.getPost(
    userRepository: UserRepository,
    postsRepository: PostsRepository
) = handleRoute(POST_GET_PATH, HttpMethod.Get) { scope, call ->
    scope.launch {
        val postId = call.receiveIntPathParameter("id") ?: return@launch
        val currentPost = postsRepository.getPost(postId)
        currentPost?.let { post ->
            call.respond(
                HttpStatusCode.OK,
                post.toResponse(userRepository.findUserById(post.userId)?.toPreviewResponse())
            )
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Post not found")
        }
    }
}

fun Route.likePost(
    postsRepository: PostsRepository
) = handleRouteWithAuth(POST_LIKE_PATH, HttpMethod.Post) { scope, call, userId ->
    scope.launch {
        val postId = call.receiveIntPathParameter("id") ?: return@launch
        val isLiked = call.receiveQueryBoolParameter("is_liked") ?: return@launch
        postsRepository.getPost(postId)?.let {
            postsRepository.likePost(userId, postId, isLiked)
            call.respond(HttpStatusCode.OK)
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Post not found")
        }
    }
}

fun Route.commentPost(
    postsRepository: PostsRepository,
    commentsRepository: CommentsRepository
) = handleRouteWithAuth(POST_COMMENT_PATH, HttpMethod.Post) { scope, call, userId ->
    scope.launch {
        val postId = call.receiveIntPathParameter("id") ?: return@launch
        val commentParams = call.receiveBodyParams<PostCommentParams>() ?: return@launch
        postsRepository.getPost(postId)?.let {
            commentsRepository.addComment(
                CommentInsertParams(
                    userId = userId,
                    postId = postId,
                    text = commentParams.text
                )
            )
            call.respond(HttpStatusCode.OK)
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Post not found")
        }
    }
}

fun Route.getPostComments(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    commentsRepository: CommentsRepository
) = handleRoute(POST_COMMENT_PATH, HttpMethod.Get) { scope, call ->
    scope.launch {
        val postId = call.receiveIntPathParameter("id") ?: return@launch
        postsRepository.getPost(postId)?.let {
            call.respond(
                HttpStatusCode.OK,
                commentsRepository.getComments(postId)
                    .map { it.toResponse(userRepository.findUserById(it.userId)?.toPreviewResponse()) }
            )
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Post not found")
        }
    }
}
