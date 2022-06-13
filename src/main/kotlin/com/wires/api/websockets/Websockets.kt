package com.wires.api.websockets

import com.wires.api.di.getKoinInstance
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import java.time.Duration

fun Application.installWebsockets() = install(WebSockets) {
    val json: Json = getKoinInstance()
    pingPeriod = Duration.ofSeconds(5)
    timeout = Duration.ofSeconds(15)
    maxFrameSize = Long.MAX_VALUE
    masking = false
    contentConverter = KotlinxWebsocketSerializationConverter(json)
}
