package com.wires.api.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    install(Authentication) {
    }
}
