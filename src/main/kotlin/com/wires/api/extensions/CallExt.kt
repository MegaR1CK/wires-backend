package com.wires.api.extensions

import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.UserUnauthorizedException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

// TODO: сделать что-то с типами receive

fun ApplicationCall.getUserId(): Int {
    return principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt() ?: throw UserUnauthorizedException()
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

suspend inline fun <reified T : Any> ApplicationCall.receiveBodyOrException(): T {
    return receiveOrNull() ?: throw MissingArgumentsException()
}

fun <T> ApplicationCall.receivePathOrException(
    name: String,
    transform: (String) -> T
): T {
    return try {
        transform(parameters[name] ?: throw MissingArgumentsException())
    } catch (throwable: Throwable) {
        throw MissingArgumentsException()
    }
}

fun <T> ApplicationCall.receiveQueryOrException(
    name: String,
    transform: (String) -> T
): T {
    return try {
        transform(request.queryParameters[name] ?: throw MissingArgumentsException())
    } catch (throwable: Throwable) {
        throw MissingArgumentsException()
    }
}
