package com.wires.api.routing

import com.wires.api.authentication.JwtService
import com.wires.api.repository.*
import com.wires.api.routing.routes.registerChannelsRoutes
import com.wires.api.routing.routes.registerPostsRoutes
import com.wires.api.routing.routes.registerStorageRoutes
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
    commentsRepository: CommentsRepository,
    channelsRepository: ChannelsRepository,
    messagesRepository: MessagesRepository,
    cryptor: Cryptor,
    jwtService: JwtService
) {
    routing {
        get("/") {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
        registerUserRoutes(userRepository, postsRepository, cryptor, jwtService)
        registerPostsRoutes(userRepository, postsRepository, commentsRepository)
        registerChannelsRoutes(userRepository, channelsRepository, messagesRepository)
        registerStorageRoutes(cryptor)
    }
}
