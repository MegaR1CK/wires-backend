package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.getUserId
import com.wires.api.extensions.proceedJsonPart
import com.wires.api.extensions.receiveBodyOrException
import com.wires.api.extensions.receiveMultipartOrException
import com.wires.api.extensions.receivePagingParams
import com.wires.api.extensions.receivePathOrException
import com.wires.api.extensions.receiveQueryOrException
import com.wires.api.extensions.respondEmpty
import com.wires.api.extensions.respondList
import com.wires.api.extensions.respondObject
import com.wires.api.routing.requestparams.PasswordChangeParams
import com.wires.api.routing.requestparams.UserEditParams
import com.wires.api.routing.requestparams.UserLoginParams
import com.wires.api.routing.requestparams.UserRegisterParams
import com.wires.api.service.UserService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

const val USER_PATH = "$API_VERSION/user"
const val USER_REGISTER_PATH = "$USER_PATH/register"
const val USER_LOGIN_PATH = "$USER_PATH/login"
const val USER_GET_BY_ID_PATH = "$USER_PATH/{id}"
const val USER_UPDATE_PATH = "$USER_PATH/update"
const val USER_CHANGE_PASSWORD_PATH = "$USER_PATH/change_password"
const val USER_GET_POSTS_PATH = "$USER_GET_BY_ID_PATH/posts"
const val USER_SEARCH = "$USER_PATH/search"

fun Routing.userController() {

    val userService: UserService by inject()

    /** Регистрация пользователя */
    post(USER_REGISTER_PATH) {
        val params = call.receiveBodyOrException<UserRegisterParams>()
        userService.registerUser(params)
        call.respondEmpty(HttpStatusCode.Created)
    }

    /** Вход пользователя в аккаунт */
    post(USER_LOGIN_PATH) {
        val params = call.receiveBodyOrException<UserLoginParams>()
        call.respondObject(HttpStatusCode.OK, userService.loginUser(params))
    }

    /** Получение пользователя по ID */
    get(USER_GET_BY_ID_PATH) {
        val userId = call.receivePathOrException("id") { it.toInt() }
        call.respondObject(HttpStatusCode.OK, userService.getUser(userId))
    }

    /** Поиск пользователей */
    get(USER_SEARCH) {
        val query = call.receiveQueryOrException("query") { it }
        call.respondList(HttpStatusCode.OK, userService.findUsers(query))
    }

    authenticate("jwt") {

        /** Получение текущего пользователя */
        get(USER_PATH) {
            call.respondObject(HttpStatusCode.OK, userService.getUser(call.getUserId()))
        }

        /** Получение постов пользователя */
        get(USER_GET_POSTS_PATH) {
            val userId = call.receivePathOrException("id") { it.toInt() }
            val pagingParams = call.receivePagingParams()
            call.respondList(
                code = HttpStatusCode.OK,
                response = userService.getUserPosts(call.getUserId(), userId, pagingParams.limit, pagingParams.offset)
            )
        }

        /** Обновление информации о текущем пользователе */
        put(USER_UPDATE_PATH) {
            var receivedUpdateParams = UserEditParams()
            var receivedAvatarBytes: ByteArray? = null
            call.receiveMultipartOrException().forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "update_params") {
                            part.proceedJsonPart<UserEditParams>()?.let { receivedUpdateParams = it }
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "avatar") receivedAvatarBytes = part.streamProvider().readBytes()
                    }
                    else -> { }
                }
            }
            userService.updateUser(call.getUserId(), receivedUpdateParams, receivedAvatarBytes)
            call.respondEmpty(HttpStatusCode.OK)
        }

        /** Смена пароля пользователя */
        put(USER_CHANGE_PASSWORD_PATH) {
            val params = call.receiveBodyOrException<PasswordChangeParams>()
            userService.changeUserPassword(call.getUserId(), params)
            call.respondEmpty(HttpStatusCode.OK)
        }
    }
}
