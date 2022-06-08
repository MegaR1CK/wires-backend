package com.wires.api.database.params

data class RefreshTokenInsertParams(
    val refreshToken: String,
    val userId: Int,
    val expiresAt: Long
)
