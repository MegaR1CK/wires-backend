package com.wires.api.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

// TODO: сделать что-то с типами receive

val ApplicationCall.userId get() = principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt()

suspend inline fun <reified T : Any> ApplicationCall.receiveBodyParams(): T? {
    return receiveOrNull() ?: run {
        respond(HttpStatusCode.BadRequest, "Missing fields")
        null
    }
}

suspend inline fun ApplicationCall.receiveIntPathParameter(name: String): Int? {
    return parameters[name]?.toIntOrNull() ?: run {
        respond(HttpStatusCode.BadRequest, "Incorrect params")
        null
    }
}

suspend inline fun ApplicationCall.receiveQueryBoolParameter(name: String): Boolean? {
    return request.queryParameters[name]?.toBooleanStrictOrNull() ?: run {
        respond(HttpStatusCode.BadRequest, "Incorrect params")
        null
    }
}
