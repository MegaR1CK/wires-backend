package com.wires.api.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

inline fun <reified T : Any> Route.postWithBodyParams(
    path: String,
    errorMessage: String = "Failed to perform request",
    crossinline block: (CoroutineScope, ApplicationCall, T) -> Unit
) {
    post(path) {
        with(call) {
            receiveOrNull<T>()?.let { params ->
                try {
                    withContext(coroutineContext) { block(this, call, params) }
                } catch (throwable: Throwable) {
                    application.log.error(errorMessage, throwable)
                    respond(HttpStatusCode.BadRequest, errorMessage)
                }
            } ?: return@post respond(HttpStatusCode.BadRequest, "Missing fields")
        }
    }
}
