package com.wires.api.websockets

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession, val userId: Int) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val name = "${lastId.getAndIncrement()}"
}
