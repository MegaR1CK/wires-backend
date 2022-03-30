package com.wires.api

import com.wires.api.authentication.installAuthentication
import com.wires.api.database.Database
import com.wires.api.di.KoinPlugin
import com.wires.api.di.WiresModule
import com.wires.api.routing.controllers.channelsController
import com.wires.api.routing.controllers.postsController
import com.wires.api.routing.controllers.userController
import com.wires.api.routing.installStatusPages
import io.ktor.http.*
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

// TODO: mappers?
// TODO: разобраться с вложенными объектами

const val API_VERSION = "/v1"

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    Database.init()
    install(KoinPlugin) {
        modules(WiresModule().module)
    }
    install(ContentNegotiation) { gson() }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter()
    }
    installAuthentication()
    installStatusPages()
    install(Routing) {
        get("/") {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
        get(API_VERSION) {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
        userController()
        postsController()
        channelsController()
    }
    install(CORS) {
        host("client-host")
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.Authorization)
    }
}
