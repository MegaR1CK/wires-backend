package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("url")
    val url: String,
    @SerializedName("size")
    val size: ImageSizeResponse
)
