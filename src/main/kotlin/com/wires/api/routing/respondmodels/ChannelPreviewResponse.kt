package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ChannelPreviewResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: ImageResponse?
)
