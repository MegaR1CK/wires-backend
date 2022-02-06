package com.wires.api.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

const val API_VERSION = "/v1"

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondFile(File("src/main/resources/static/index.html"))
        }
    }
}
