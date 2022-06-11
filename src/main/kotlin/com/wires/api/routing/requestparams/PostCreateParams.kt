package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostCreateParams(
    @SerialName("text")
    val text: String,
    @SerialName("topic")
    val topic: String
)
