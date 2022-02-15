package com.wires.api.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

val ApplicationCall.userId get() = principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt()

suspend inline fun <reified T : Any> ApplicationCall.receiveBodyParams(block: (T) -> Unit) {
    receiveOrNull<T>()?.let {
        block(it)
    } ?: run {
        respond(HttpStatusCode.BadRequest, "Missing fields")
    }
}
