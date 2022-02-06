package com.wires.api

import com.wires.api.authentication.configureAuthentication
import com.wires.api.database.Database
import com.wires.api.routing.configureRouting
import com.wires.api.serialization.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureAuthentication()
    configureRouting()
    Database.init()
}
