package com.wires.api.routing.respondmodels

import com.wires.api.model.ChannelType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String?,
    @SerialName("type")
    val type: ChannelType,
    @SerialName("image")
    val image: ImageResponse?,
    @SerialName("members")
    val members: List<UserPreviewResponse>
)
