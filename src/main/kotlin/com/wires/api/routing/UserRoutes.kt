package com.wires.api.routing

import com.wires.api.database.params.InsertUserParams
import com.wires.api.extensions.postWithBodyParams
import com.wires.api.repository.UserRepository
import com.wires.api.routing.requestparams.RegisterUserParams
import com.wires.api.utils.Cryptor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

const val USER_PATH = "$API_VERSION/user"
const val USER_REGISTER_PATH = "$USER_PATH/register"

fun Application.registerUserRoutes(userRepository: UserRepository, cryptor: Cryptor) {
    routing {
        registerUser(userRepository, cryptor)
    }
}

fun Route.registerUser(
    userRepository: UserRepository,
    cryptor: Cryptor
) = postWithBodyParams<RegisterUserParams>(USER_REGISTER_PATH) { scope, call, params ->
    scope.launch {
        if (userRepository.findUserByEmail(params.email) == null) {
            val salt = cryptor.generateSalt()
            val newUser = InsertUserParams(
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
