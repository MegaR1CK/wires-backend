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
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.ksp.generated.module
import org.slf4j.event.Level
import java.io.File
import java.time.Duration

const val API_VERSION = "/v1"

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    Database.init()
    install(KoinPlugin) {
        modules(WiresModule().module)
    }
    install(CallLogging) {
        level = Level.INFO
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
        anyHost()
        methods.addAll(
            listOf(
                HttpMethod.Get,
                HttpMethod.Post,
                HttpMethod.Put,
                HttpMethod.Delete
            )
        )
        headers.addAll(
            listOf(
                HttpHeaders.ContentType,
                HttpHeaders.Authorization,
                HttpHeaders.AccessControlAllowOrigin
            )
        )
        allowNonSimpleContentTypes = true
        allowSameOrigin = true
    }
}
