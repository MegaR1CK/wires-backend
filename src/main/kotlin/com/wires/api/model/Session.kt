package com.wires.api.model

data class Session(
    val deviceId: String,
    val userId: Int,
    val refreshToken: String,
    val expiresAt: Long
)
