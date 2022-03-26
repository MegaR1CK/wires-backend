package com.wires.api

import com.cloudinary.Cloudinary
import com.wires.api.authentication.installAuthentication
import com.wires.api.database.Database
import com.wires.api.di.KoinPlugin
import com.wires.api.di.WiresModule
import com.wires.api.repository.ChannelsRepository
import com.wires.api.repository.CommentsRepository
import com.wires.api.repository.MessagesRepository
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.controllers.registerChannelsRoutes
import com.wires.api.routing.controllers.registerPostsRoutes
import com.wires.api.routing.controllers.registerStorageRoutes
import com.wires.api.routing.controllers.userController
import com.wires.api.routing.installStatusPages
import com.wires.api.utils.DateFormatter
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ksp.generated.module
import java.io.File
import java.time.Duration

// TODO: paging
// TODO: di
// TODO: service (clean arch)
// TODO: mappers?

const val API_VERSION = "/v1"

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val userRepository = UserRepository()
    val postsRepository = PostsRepository()
    val commentsRepository = CommentsRepository()
    val channelsRepository = ChannelsRepository()
    val messagesRepository = MessagesRepository()
    val storageRepository = StorageRepository(Cloudinary())
    val dateFormatter = DateFormatter()
    Database.init()
    install(KoinPlugin) {
        modules(WiresModule().module)
    }
    installStatusPages()
    install(ContentNegotiation) { gson() }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter()
    }
    installAuthentication()
    install(Routing) {
        get("/") {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
        userController()
        registerPostsRoutes(userRepository, postsRepository, commentsRepository, storageRepository, dateFormatter)
        registerChannelsRoutes(userRepository, channelsRepository, messagesRepository, dateFormatter)
        registerStorageRoutes(storageRepository)
    }
}
