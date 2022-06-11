package com.wires.api.authentication

data class GeneratedTokens(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
)
