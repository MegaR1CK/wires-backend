package com.wires.api.routing

import com.wires.api.authentication.JwtService
import com.wires.api.repository.*
import com.wires.api.routing.routes.registerChannelsRoutes
import com.wires.api.routing.routes.registerPostsRoutes
import com.wires.api.routing.routes.registerStorageRoutes
import com.wires.api.routing.routes.registerUserRoutes
import com.wires.api.utils.Cryptor
import com.wires.api.utils.DateFormatter
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
    storageRepository: StorageRepository,
    dateFormatter: DateFormatter,
    cryptor: Cryptor,
    jwtService: JwtService
) {
    routing {
        get("/") {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
        registerUserRoutes(userRepository, postsRepository, dateFormatter, cryptor, jwtService)
        registerPostsRoutes(userRepository, postsRepository, commentsRepository, storageRepository, dateFormatter)
        registerChannelsRoutes(userRepository, channelsRepository, messagesRepository, dateFormatter)
        registerStorageRoutes(storageRepository)
    }
}
