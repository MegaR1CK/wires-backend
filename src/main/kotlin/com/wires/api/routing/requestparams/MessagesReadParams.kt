package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessagesReadParams(
    @SerialName("messages_ids")
    val messagesIds: List<Int>
)
