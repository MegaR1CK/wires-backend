package com.wires.api.routing.routes

import com.wires.api.authentication.JwtService
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.extensions.*
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.UserEditParams
import com.wires.api.routing.requestparams.UserLoginParams
import com.wires.api.routing.requestparams.UserRegisterParams
import com.wires.api.routing.respondmodels.TokenResponse
import com.wires.api.utils.Cryptor
import com.wires.api.utils.DateFormatter
import io.ktor.http.*
import io.ktor.server.application.*
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

fun Application.registerUserRoutes(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    dateFormatter: DateFormatter,
    cryptor: Cryptor,
    jwtService: JwtService
) = routing {
    registerUser(userRepository, cryptor)
    loginUser(userRepository, cryptor, jwtService)
    getCurrentUser(userRepository)
    getUserById(userRepository)
    updateUser(userRepository, cryptor)
    getUserPosts(userRepository, postsRepository, dateFormatter)
}

fun Route.registerUser(
    userRepository: UserRepository,
    cryptor: Cryptor
) = handleRoute(USER_REGISTER_PATH, HttpMethod.Post) { scope, call ->
    scope.launch {
        val params = call.receiveBodyParams<UserRegisterParams>() ?: return@launch
        if (userRepository.findUserByEmail(params.email) == null) {
            val salt = cryptor.generateSalt()
            cryptor.getBcryptHash(params.passwordHash, salt)?.let { hash ->
                val newUser = UserInsertParams(
                    email = params.email,
                    username = params.username,
                    passwordHash = hash,
                    passwordSalt = salt
                )
                userRepository.registerUser(newUser)
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.BadRequest, "Incorrect params")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "User already exists")
        }
    }
}

fun Route.loginUser(
    userRepository: UserRepository,
    cryptor: Cryptor,
    jwtService: JwtService
) = handleRoute(USER_LOGIN_PATH, HttpMethod.Post) { scope, call ->
    scope.launch {
        val params = call.receiveBodyParams<UserLoginParams>() ?: return@launch
        val currentUser = userRepository.findUserByEmail(params.email)
        if (currentUser != null &&
            cryptor.checkBcryptHash(params.passwordHash, currentUser.passwordSalt, currentUser.passwordHash)
        ) {
            call.respond(HttpStatusCode.OK, TokenResponse(jwtService.generateToken(currentUser)))
        } else call.respond(HttpStatusCode.Unauthorized, "Incorrect credentials")
    }
}

fun Route.getCurrentUser(
    userRepository: UserRepository
) = handleRouteWithAuth(USER_PATH, HttpMethod.Get) { scope, call, userId ->
    scope.launch {
        val currentUser = userRepository.findUserById(userId)
        currentUser?.let { user ->
            call.respond(HttpStatusCode.OK, user.toResponse())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.getUserById(
    userRepository: UserRepository
) = handleRoute(USER_GET_BY_ID_PATH, HttpMethod.Get) { scope, call ->
    scope.launch {
        val userId = call.receiveIntPathParameter("id") ?: return@launch
        userRepository.findUserById(userId)?.let { user ->
            call.respond(HttpStatusCode.OK, user.toResponse())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.updateUser(
    userRepository: UserRepository,
    cryptor: Cryptor
) = handleRouteWithAuth(USER_UPDATE_PATH, HttpMethod.Put) { scope, call, userId ->
    scope.launch {
        val updateParams = call.receiveOrNull<UserEditParams>()
            ?: return@launch call.respond(HttpStatusCode.BadRequest, "Missing fields")
        val salt = cryptor.generateSalt().takeIf { updateParams.passwordHash != null }
        userRepository.updateUser(
            UserUpdateParams(
                id = userId,
                username = updateParams.username,
                email = updateParams.email,
                passwordHash = cryptor.getBcryptHash(updateParams.passwordHash, salt),
                passwordSalt = salt,
                avatarUrl = updateParams.avatarUrl
            )
        )
        call.respond(HttpStatusCode.OK)
    }
}

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
