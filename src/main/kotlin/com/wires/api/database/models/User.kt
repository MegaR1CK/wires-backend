package com.wires.api.database.models

import io.ktor.server.auth.*

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String
) : Principal
