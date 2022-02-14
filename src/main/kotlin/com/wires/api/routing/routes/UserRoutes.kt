package com.wires.api.routing.routes

import com.wires.api.authentication.JwtService
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.extensions.handleRouteWithAuth
import com.wires.api.extensions.handleRouteWithBodyParams
import com.wires.api.extensions.handleRouteWithPathParams
import com.wires.api.repository.UserRepository
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.UserEditParams
import com.wires.api.routing.requestparams.UserLoginParams
import com.wires.api.routing.requestparams.UserRegisterParams
import com.wires.api.routing.respondmodels.TokenResponse
import com.wires.api.utils.Cryptor
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

fun Application.registerUserRoutes(userRepository: UserRepository, cryptor: Cryptor, jwtService: JwtService) {
    routing {
        registerUser(userRepository, cryptor)
        loginUser(userRepository, cryptor, jwtService)
        getCurrentUser(userRepository)
        getUserById(userRepository)
        updateUser(userRepository, cryptor)
    }
}

fun Route.registerUser(
    userRepository: UserRepository,
    cryptor: Cryptor
) = handleRouteWithBodyParams<UserRegisterParams>(USER_REGISTER_PATH, HttpMethod.Post) { scope, call, params ->
    scope.launch {
        if (userRepository.findUserByEmail(params.email) == null) {
            val salt = cryptor.generateSalt()
            val newUser = UserInsertParams(
                email = params.email,
                username = params.username,
                passwordHash = cryptor.getBcryptHash(params.passwordHash, salt),
                passwordSalt = salt
            )
            userRepository.registerUser(newUser)
            call.respond(HttpStatusCode.Created)
        } else {
            call.respond(HttpStatusCode.BadRequest, "User already exists")
        }
    }
}

fun Route.loginUser(
    userRepository: UserRepository,
    cryptor: Cryptor,
    jwtService: JwtService
) = handleRouteWithBodyParams<UserLoginParams>(USER_LOGIN_PATH, HttpMethod.Post) { scope, call, params ->
    scope.launch {
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
) = handleRouteWithPathParams(USER_GET_BY_ID_PATH, HttpMethod.Get) { scope, call, params ->
    scope.launch {
        params["id"]?.toIntOrNull()?.let { userId ->
            userRepository.findUserById(userId)?.let { user ->
                call.respond(HttpStatusCode.OK, user.toResponse())
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Incorrect id")
        }
    }
}

fun Route.updateUser(
    userRepository: UserRepository,
    cryptor: Cryptor
) = handleRouteWithAuth(USER_UPDATE_PATH, HttpMethod.Put) { scope, call, userId ->
    scope.launch {
        val currentUser = userRepository.findUserById(userId)
        currentUser?.let { user ->
            val updateParams = call.receiveOrNull<UserEditParams>()
                ?: return@launch call.respond(HttpStatusCode.BadRequest, "Missing fields")
            userRepository.updateUser(
                UserUpdateParams(
                    id = userId,
                    username = updateParams.username,
                    email = updateParams.email,
                    passwordHash = cryptor.getBcryptHash(updateParams.passwordHash, user.passwordSalt)
                )
            )
            call.respond(HttpStatusCode.OK)
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}
