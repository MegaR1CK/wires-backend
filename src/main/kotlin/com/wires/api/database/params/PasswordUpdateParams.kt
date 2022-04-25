package com.wires.api.database.params

data class PasswordUpdateParams(
    val id: Int,
    val newPasswordHash: String,
    val newPasswordSalt: String
)
