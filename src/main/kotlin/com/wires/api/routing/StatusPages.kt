package com.wires.api.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*

fun Application.installStatusPages() = install(StatusPages) {
    exception<UserExistsException> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message)
    }
    exception<UserUnauthorizedException> { call, cause ->
        call.respond(HttpStatusCode.Unauthorized, cause.message)
    }
    exception<WrongCredentialsException> { call, cause ->
        call.respond(HttpStatusCode.Unauthorized, cause.message)
    }
    exception<MissingArgumentsException> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message)
    }
    exception<ForbiddenException> { call, cause ->
        call.respond(HttpStatusCode.Forbidden, cause.message)
    }
    exception<NotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message)
    }
    exception<StorageException> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message)
    }
    exception<UnknownError> { call, _ ->
        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
    }
}

data class UserExistsException(override val message: String = "User already exists") : Exception()
data class UserUnauthorizedException(override val message: String = "User unauthorized") : Exception()
data class WrongCredentialsException(override val message: String = "Wrong credentials") : Exception()
data class MissingArgumentsException(override val message: String = "Missing arguments") : Exception()
data class ForbiddenException(override val message: String = "You haven't access to this data") : Exception()
data class NotFoundException(override val message: String = "Element not found") : Exception()
data class StorageException(override val message: String = "Failed to add file in storage") : Exception()
