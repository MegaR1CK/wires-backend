package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLogoutParams(
    @SerialName("device_id")
    val deviceId: String
)
