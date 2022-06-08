package com.wires.api.database.params

data class RefreshTokenUpdateParams(
    val oldRefreshToken: String,
    val newRefreshToken: String,
    val newExpiresAt: Long
)
