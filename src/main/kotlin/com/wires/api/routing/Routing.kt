package com.wires.api.routing

import com.wires.api.authentication.JwtService
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.routes.registerPostsRoutes
import com.wires.api.routing.routes.registerUserRoutes
import com.wires.api.utils.Cryptor
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

const val API_VERSION = "/v1"

fun Application.configureRouting(
    userRepository: UserRepository,
    postsRepository: PostsRepository,
    cryptor: Cryptor,
    jwtService: JwtService
) {
    routing {
        get("/") {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
        registerUserRoutes(userRepository, cryptor, jwtService)
        registerPostsRoutes(userRepository, postsRepository)
    }
}
