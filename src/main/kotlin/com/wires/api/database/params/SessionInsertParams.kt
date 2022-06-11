package com.wires.api.database.params

data class SessionInsertParams(
    val deviceId: String,
    val userId: Int,
    val refreshToken: String,
    val expiresAt: Long
)
