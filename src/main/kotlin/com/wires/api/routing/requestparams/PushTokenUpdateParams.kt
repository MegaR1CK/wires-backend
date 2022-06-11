package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushTokenUpdateParams(
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("push_token")
    val pushToken: String
)
