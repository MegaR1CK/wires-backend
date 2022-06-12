package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushTokenRegisterParams(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("push_token")
    val pushToken: String?
)
