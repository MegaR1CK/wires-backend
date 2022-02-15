package com.wires.api.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

fun Route.handleRoute(
    path: String,
    method: HttpMethod,
    errorMessage: String = "Failed to perform request",
    block: (CoroutineScope, ApplicationCall) -> Unit
) {
    route(path, method) {
        handle {
            with(call) {
                try {
                    withContext(coroutineContext) { block(this, call) }
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
