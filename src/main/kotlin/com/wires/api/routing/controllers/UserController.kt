package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.*
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.requestparams.UserEditParams
import com.wires.api.routing.requestparams.UserLoginParams
import com.wires.api.routing.requestparams.UserRegisterParams
import com.wires.api.service.UserService
import com.wires.api.utils.DateFormatter
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

const val USER_PATH = "$API_VERSION/user"
const val USER_REGISTER_PATH = "$USER_PATH/register"
const val USER_LOGIN_PATH = "$USER_PATH/login"
const val USER_GET_BY_ID_PATH = "$USER_PATH/{id}"
const val USER_UPDATE_PATH = "$USER_PATH/update"
const val USER_GET_POSTS_PATH = "$USER_GET_BY_ID_PATH/posts"

fun Routing.userController() {

    val userService: UserService by inject()

    post(USER_REGISTER_PATH) {
        val params = call.receiveOrException<UserRegisterParams>()
        userService.registerUser(params)
        call.respond(HttpStatusCode.Created)
    }

    post(USER_LOGIN_PATH) {
        val params = call.receiveOrException<UserLoginParams>()
        call.respond(HttpStatusCode.OK, userService.loginUser(params))
    }

    get(USER_GET_BY_ID_PATH) {
        val id = call.parameters["id"]?.toIntOrNull()
        call.respond(HttpStatusCode.OK, userService.getUser(id))
    }

    authenticate("jwt") {
        get(USER_PATH) {
            call.respond(HttpStatusCode.OK, userService.getUser(call.getUserId()))
        }

        put(USER_UPDATE_PATH) {
            var receivedUpdateParams: UserEditParams? = null
            var receivedAvatarBytes: ByteArray? = null
            call.receiveMultipart().forEachPart { part ->
                when (part) {
                    is PartData.FormItem ->
                        if (part.name == "update_params") receivedUpdateParams = part.proceedJsonPart<UserEditParams>()
                    is PartData.FileItem ->
                        if (part.name == "avatar") receivedAvatarBytes = part.streamProvider().readBytes()
                    else -> { }
                }
            }
            userService.updateUser(call.getUserId(), receivedUpdateParams, receivedAvatarBytes)
            call.respond(HttpStatusCode.OK)
        }
    }
}

// TODO: to posts controller
fun Route.getUserPosts(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    dateFormatter: DateFormatter
) = handleRoute(USER_GET_POSTS_PATH, HttpMethod.Get) { scope, call ->
    scope.launch {
        val userId = call.receiveIntPathParameter("id") ?: return@launch
        call.respond(
            HttpStatusCode.OK,
            postsRepository.getUserPosts(userId).map { post ->
                post.toResponse(
                    userRepository.findUserById(userId)?.toPreviewResponse(),
                    dateFormatter.dateTimeToFullString(post.publishTime)
                )
            }
        )
    }
}
