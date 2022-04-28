package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ChannelPreviewResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("image")
    val image: ImageResponse?,
    @SerializedName("last_message")
    val lastMessage: MessageResponse?
)
