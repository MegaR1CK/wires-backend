package com.wires.api.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

inline fun <reified T : Any> Route.handleRouteWithBodyParams(
    path: String,
    method: HttpMethod,
    errorMessage: String = "Failed to perform request",
    crossinline block: (CoroutineScope, ApplicationCall, T) -> Unit
) {
    route(path, method) {
        handle {
            with(call) {
                receiveOrNull<T>()?.let { params ->
                    try {
                        withContext(coroutineContext) { block(this, call, params) }
                    } catch (throwable: Throwable) {
                        application.log.error(errorMessage, throwable)
                        respond(HttpStatusCode.BadRequest, errorMessage)
                    }
                } ?: return@handle respond(HttpStatusCode.BadRequest, "Missing fields")
            }
        }
    }
}

fun Route.handleRouteWithPathParams(
    path: String,
    method: HttpMethod,
    errorMessage: String = "Failed to perform request",
    block: (CoroutineScope, ApplicationCall, Parameters) -> Unit
) {
    route(path, method) {
        handle {
            with(call) {
                try {
                    withContext(coroutineContext) { block(this, call, parameters) }
                } catch (throwable: Throwable) {
                    application.log.error(errorMessage, throwable)
                    respond(HttpStatusCode.BadRequest, errorMessage)
                }
            }
        }
    }
}

fun Route.handleRouteWithAuth(
    path: String,
    method: HttpMethod,
    errorMessage: String = "Failed to perform request",
    block: (CoroutineScope, ApplicationCall, Int) -> Unit
) {
    authenticate("jwt") {
        route(path, method) {
            handle {
                call.userId?.let { userId ->
                    try {
                        withContext(coroutineContext) { block(this, call, userId) }
                    } catch (throwable: Throwable) {
                        application.log.error(errorMessage, throwable)
                        call.respond(HttpStatusCode.BadRequest, errorMessage)
                    }
                } ?: return@handle call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
