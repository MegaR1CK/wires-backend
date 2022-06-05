package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ChannelResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String,
    @SerializedName("image")
    val image: ImageResponse?,
    @SerializedName("members")
    val members: List<UserPreviewResponse>
)
