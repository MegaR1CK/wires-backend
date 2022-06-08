package com.wires.api.model

data class RefreshToken(
    val refreshToken: String,
    val userId: Int,
    val expiresAt: Long
)
