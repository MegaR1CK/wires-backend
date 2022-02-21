package com.wires.api.database.models

import com.wires.api.routing.respondmodels.UserPreviewResponse
import com.wires.api.routing.respondmodels.UserResponse
import io.ktor.server.auth.*

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String,
    val avatarUrl: String?,
    val interests: List<String>,
    val channels: List<Int>
) : Principal {
    fun toResponse() = UserResponse(
        id = id,
        email = email,
        username = username,
        avatarUrl = avatarUrl,
        interests = interests
    )

    fun toPreviewResponse() = UserPreviewResponse(
        id = id,
        username = username,
        avatarUrl = avatarUrl
    )
}
