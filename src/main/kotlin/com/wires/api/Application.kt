package com.wires.api

import com.wires.api.authentication.installAuthentication
import com.wires.api.database.Database
import com.wires.api.di.KoinPlugin
import com.wires.api.di.WiresModule
import com.wires.api.di.getKoinInstance
import com.wires.api.firebase.installFirebase
import com.wires.api.routing.installCors
import com.wires.api.routing.installRouting
import com.wires.api.routing.installStatusPages
import com.wires.api.websockets.installWebsockets
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.ksp.generated.module
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    Database.init()
    installFirebase()
    install(KoinPlugin) { modules(WiresModule().module) }
    install(CallLogging) { level = Level.INFO }
    install(ContentNegotiation) { json(getKoinInstance()) }
    installWebsockets()
    installAuthentication()
    installStatusPages()
    installRouting()
    installCors()
}
