package com.wires.api.routing

import com.wires.api.routing.controllers.channelsController
import com.wires.api.routing.controllers.devicesController
import com.wires.api.routing.controllers.postsController
import com.wires.api.routing.controllers.userController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

const val API_VERSION = "/v1"

fun Application.installRouting() = install(Routing) {
    get("/") {
        call.respondFile(File("src/main/resources/static/index.html"))
    }
    get(API_VERSION) {
        call.respondFile(File("src/main/resources/static/index.html"))
    }
    userController()
    postsController()
    channelsController()
    devicesController()
}

fun Application.installCors() = install(CORS) {
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
