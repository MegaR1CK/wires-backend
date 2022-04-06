package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.getUserId
import com.wires.api.extensions.proceedJsonPart
import com.wires.api.extensions.receiveBodyOrException
import com.wires.api.extensions.receivePagingParams
import com.wires.api.extensions.receivePathOrException
import com.wires.api.extensions.receiveQueryOrException
import com.wires.api.extensions.respondEmpty
import com.wires.api.extensions.respondObject
import com.wires.api.routing.requestparams.PostCommentParams
import com.wires.api.routing.requestparams.PostCreateParams
import com.wires.api.service.PostsService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val POSTS_PATH = "$API_VERSION/posts"
private const val POST_CREATE_PATH = "$POSTS_PATH/create"
private const val POST_GET_PATH = "$POSTS_PATH/{id}"
private const val POST_LIKE_PATH = "$POST_GET_PATH/like"
private const val POST_COMMENT_PATH = "$POST_GET_PATH/comment"

fun Routing.postsController() {

    val postsService: PostsService by inject()

    authenticate("jwt") {
        /** Получение подборки постов */
        get(POSTS_PATH) {
            // Получаем topic обычным способом, так как он необязтателен
            val topic = call.request.queryParameters["topic"]
            val pagingParams = call.receivePagingParams()
            val posts = if (topic != null) {
                postsService.getPostsByTopic(topic, pagingParams.limit, pagingParams.offset)
            } else {
                postsService.getPostsCompilation(call.getUserId(), pagingParams.limit, pagingParams.offset)
            }
            call.respond(HttpStatusCode.OK, posts)
        }

        /** Создание поста */
        post(POST_CREATE_PATH) {
            var receivedPostParams: PostCreateParams? = null
            var receivedPictureBytes: ByteArray? = null
            call.receiveMultipart().forEachPart { part ->
                when (part) {
                    is PartData.FormItem ->
                        if (part.name == "create_params") receivedPostParams = part.proceedJsonPart<PostCreateParams>()
                    is PartData.FileItem ->
                        if (part.name == "image") receivedPictureBytes = part.streamProvider().readBytes()
                    else -> { }
                }
            }
            postsService.createPost(call.getUserId(), receivedPostParams, receivedPictureBytes)
            call.respondEmpty(HttpStatusCode.OK)
        }

        /** Установка лайка на пост */
        post(POST_LIKE_PATH) {
            val postId = call.receivePathOrException("id") { it.toInt() }
            val isLiked = call.receiveQueryOrException("is_liked") { it.toBooleanStrict() }
            postsService.likePost(call.getUserId(), postId, isLiked)
            call.respondEmpty(HttpStatusCode.OK)
        }

        /** Добавление комментария под пост */
        post(POST_COMMENT_PATH) {
            val postId = call.receivePathOrException("id") { it.toInt() }
            val commentParams = call.receiveBodyOrException<PostCommentParams>()
            postsService.commentPost(call.getUserId(), postId, commentParams)
            call.respondEmpty(HttpStatusCode.OK)
        }
    }

    /** Получение информации о посте */
    get(POST_GET_PATH) {
        val postId = call.receivePathOrException("id") { it.toInt() }
        call.respondObject(HttpStatusCode.OK, postsService.getPost(postId))
    }

    /** Получение комментариев под постом */
    get(POST_COMMENT_PATH) {
        val postId = call.receivePathOrException("id") { it.toInt() }
        val pagingParams = call.receivePagingParams()
        call.respond(HttpStatusCode.OK, postsService.getPostComments(postId, pagingParams.limit, pagingParams.offset))
    }
}
