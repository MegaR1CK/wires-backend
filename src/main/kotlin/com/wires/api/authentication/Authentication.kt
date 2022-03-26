package com.wires.api.authentication

import com.wires.api.di.inject
import com.wires.api.repository.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.installAuthentication() = install(Authentication) {
    val userRepository: UserRepository by inject()
    val jwtService: JwtService by inject()
    jwt("jwt") {
        verifier(jwtService.verifier)
        realm = "Wires API"
        validate { credential ->
            if (userRepository.findUserById(credential.payload.getClaim("id").asInt()) != null) {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }
    }
}
