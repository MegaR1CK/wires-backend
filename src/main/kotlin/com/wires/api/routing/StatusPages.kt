package com.wires.api.routing

import com.wires.api.extensions.respondError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*

fun Application.installStatusPages() = install(StatusPages) {
    exception<EmailExistsException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<UsernameTakenException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<UserUnauthorizedException> { call, cause ->
        call.respondError(HttpStatusCode.Unauthorized, cause.message)
    }
    exception<WrongCredentialsException> { call, cause ->
        call.respondError(HttpStatusCode.Forbidden, cause.message)
    }
    exception<MissingArgumentsException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<ForbiddenException> { call, cause ->
        call.respondError(HttpStatusCode.Forbidden, cause.message)
    }
    exception<NotFoundException> { call, cause ->
        call.respondError(HttpStatusCode.NotFound, cause.message)
    }
    exception<StorageException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<SocketException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<PersonalChannelExistsException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<RefreshTokenExpiredException> { call, cause ->
        call.respondError(HttpStatusCode.Forbidden, cause.message)
    }
    exception<WrongChannelTypeException> { call, cause ->
        call.respondError(HttpStatusCode.BadRequest, cause.message)
    }
    exception<UnknownError> { call, _ ->
        call.respondError(HttpStatusCode.InternalServerError, "Internal server error")
    }
}

data class EmailExistsException(override val message: String = "User with such email already exists") : Exception()
data class UsernameTakenException(override val message: String = "This username is already taken") : Exception()
data class UserUnauthorizedException(override val message: String = "User unauthorized") : Exception()
data class WrongCredentialsException(override val message: String = "Wrong credentials") : Exception()
data class MissingArgumentsException(override val message: String = "Missing arguments") : Exception()
data class ForbiddenException(override val message: String = "You haven't access to this data") : Exception()
data class NotFoundException(override val message: String = "Element not found") : Exception()
data class SocketException(override val message: String = "WebSocket error") : Exception()
data class StorageException(override val message: String = "Failed to add file in storage") : Exception()
data class RefreshTokenExpiredException(override val message: String = "Refresh token expired") : Exception()
data class PersonalChannelExistsException(
    override val message: String = "Personal channel with this user already exists"
) : Exception()
data class WrongChannelTypeException(override val message: String = "Wrong channel type") : Exception()
data class DeletingOwnerException(override val message: String = "Cannot delete channel owner") : Exception()
