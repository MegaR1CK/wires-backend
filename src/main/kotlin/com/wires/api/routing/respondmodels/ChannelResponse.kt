package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName
import com.wires.api.model.ChannelType

data class ChannelResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: ChannelType,
    @SerializedName("image")
    val image: ImageResponse?,
    @SerializedName("members")
    val members: List<UserPreviewResponse>
)
