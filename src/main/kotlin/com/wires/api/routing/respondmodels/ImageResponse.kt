package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("url")
    val url: String,
    @SerialName("size")
    val size: ImageSizeResponse
)
