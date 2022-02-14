package com.wires.api.database.models

import com.wires.api.routing.respondmodels.UserResponse
import io.ktor.server.auth.*

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String,
    val interests: List<String>
) : Principal {
    fun toResponse() = UserResponse(
        id = id,
        email = email,
        username = username,
        interests = interests
    )
}
