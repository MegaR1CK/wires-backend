package com.wires.api.extensions

import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.UserUnauthorizedException
import com.wires.api.routing.requestparams.PagingParams
import com.wires.api.routing.respondmodels.ObjectResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun ApplicationCall.getUserId(): Int {
    return principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt() ?: throw UserUnauthorizedException()
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

fun ApplicationCall.receivePagingParams(): PagingParams {
    return PagingParams(
        limit = receiveQueryOrException("limit") { it.toInt() },
        offset = receiveQueryOrException("offset") { it.toLong() }
    )
}

suspend fun <T> ApplicationCall.respondObject(code: HttpStatusCode, response: T) {
    respond(code, ObjectResponse(response))
}
