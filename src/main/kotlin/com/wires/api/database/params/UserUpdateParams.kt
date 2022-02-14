package com.wires.api.database.params

data class UserUpdateParams(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String
)
