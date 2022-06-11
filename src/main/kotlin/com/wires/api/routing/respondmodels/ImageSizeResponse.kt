package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageSizeResponse(
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int
)
