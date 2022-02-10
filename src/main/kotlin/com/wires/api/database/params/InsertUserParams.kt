package com.wires.api.database.params

data class InsertUserParams(
    val email: String,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String
)
