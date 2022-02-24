package com.wires.api.routing.routes

import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.params.PostInsertParams
import com.wires.api.extensions.*
import com.wires.api.repository.CommentsRepository
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.PostCommentParams
import com.wires.api.routing.requestparams.PostCreateParams
import com.wires.api.utils.DateFormatter
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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
    commentsRepository: CommentsRepository,
    storageRepository: StorageRepository,
    dateFormatter: DateFormatter
) = routing {
    getPostsCompilation(userRepository, postsRepository, dateFormatter)
    createPost(postsRepository, storageRepository)
    getPost(userRepository, postsRepository, dateFormatter)
    likePost(postsRepository)
    commentPost(postsRepository, commentsRepository)
    getPostComments(userRepository, postsRepository, commentsRepository, dateFormatter)
}

fun Route.getPostsCompilation(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    dateFormatter: DateFormatter
) = handleRouteWithAuth(POSTS_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        val topic = call.request.queryParameters["topic"]
        if (topic == null) {
            val currentUser = userRepository.findUserById(userId)
            currentUser?.let { user ->
                call.respond(
                    HttpStatusCode.OK,
                    postsRepository.getPostsList(user.interests)
                        .map { post ->
                            post.toResponse(
                                userRepository.findUserById(post.userId)?.toPreviewResponse(),
                                dateFormatter.dateTimeToFullString(post.publishTime)
                            )
                        }
                )
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        } else {
            call.respond(
                HttpStatusCode.OK,
                postsRepository.getPostsList(listOf(topic)).map { post ->
                    post.toResponse(
                        userRepository.findUserById(post.userId)?.toPreviewResponse(),
                        dateFormatter.dateTimeToFullString(post.publishTime)
                    )
                }
            )
        }
    }
}
// TODO: return image size
fun Route.createPost(
    postsRepository: PostsRepository,
    storageRepository: StorageRepository
) = handleRouteWithAuth(POST_CREATE_PATH, HttpMethod.Post) { scope, call, userId ->
    scope.launch {
        var receivedPostParams: PostCreateParams? = null
        var receivedPictureBytes: ByteArray? = null
        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FormItem ->
                    if (part.name == "post") receivedPostParams = part.proceedJsonPart<PostCreateParams>()
                is PartData.FileItem ->
                    if (part.name == "image") receivedPictureBytes = part.streamProvider().readBytes()
                else -> { }
            }
        }
        receivedPostParams?.let { params ->
            val imageUrl = receivedPictureBytes?.let { bytes ->
                storageRepository.uploadFile(bytes)
                    ?: return@launch call.respond(HttpStatusCode.BadRequest, "Failed to perform request")
            }
            val insertParams = PostInsertParams(
                text = params.text,
                imageUrl = imageUrl,
                topic = params.topic,
                userId = userId
            )
            postsRepository.createPost(insertParams)
            call.respond(HttpStatusCode.OK)
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Incorrect params")
        }
    }
}

fun Route.getPost(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    dateFormatter: DateFormatter
) = handleRoute(POST_GET_PATH, HttpMethod.Get) { scope, call ->
    scope.launch {
        val postId = call.receiveIntPathParameter("id") ?: return@launch
        val currentPost = postsRepository.getPost(postId)
        currentPost?.let { post ->
            call.respond(
                HttpStatusCode.OK,
                post.toResponse(
                    userRepository.findUserById(post.userId)?.toPreviewResponse(),
                    dateFormatter.dateTimeToFullString(post.publishTime)
                )
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
    commentsRepository: CommentsRepository,
    dateFormatter: DateFormatter
) = handleRoute(POST_COMMENT_PATH, HttpMethod.Get) { scope, call ->
    scope.launch {
        val postId = call.receiveIntPathParameter("id") ?: return@launch
        postsRepository.getPost(postId)?.let {
            call.respond(
                HttpStatusCode.OK,
                commentsRepository.getComments(postId)
                    .map { comment ->
                        comment.toResponse(
                            userRepository.findUserById(comment.userId)?.toPreviewResponse(),
                            dateFormatter.dateTimeToFullString(comment.sendTime)
                        )
                    }
            )
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Post not found")
        }
    }
}
