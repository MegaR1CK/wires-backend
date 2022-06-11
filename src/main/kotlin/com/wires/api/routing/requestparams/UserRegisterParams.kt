package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterParams(
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("password_hash")
    val passwordHash: String
)
