package com.wires.api.extensions

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

val ApplicationCall.userId get() = principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt()
