package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLoginParams(
    @SerialName("email")
    val email: String,
    @SerialName("password_hash")
    val passwordHash: String,
    @SerialName("device_id")
    val deviceId: String
)
