package com.wires.api

import com.wires.api.authentication.configureAuthentication
import com.wires.api.database.Database
import com.wires.api.repository.UserRepository
import com.wires.api.routing.configureRouting
import com.wires.api.serialization.configureSerialization
import com.wires.api.utils.Cryptor
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val userRepository = UserRepository()
    val cryptor = Cryptor()
    Database.init()
    configureSerialization()
    configureAuthentication()
    configureRouting(userRepository, cryptor)
}
