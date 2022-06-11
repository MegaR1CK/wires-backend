package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostEditParams(
    @SerialName("text")
    val text: String?,
    @SerialName("topic")
    val topic: String?
)
