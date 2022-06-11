package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageSendParams(
    @SerialName("text")
    val text: String,
    @SerialName("is_initial")
    val isInitial: Boolean
)
