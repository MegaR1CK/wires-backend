package com.wires.api.routing.respondmodels

import com.wires.api.model.ChannelType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelPreviewResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String?,
    @SerialName("type")
    val type: ChannelType,
    @SerialName("image")
    val image: ImageResponse?,
    @SerialName("last_message")
    val lastMessage: MessageResponse?,
    @SerialName("unread_messages")
    val unreadMessages: Int,
    @SerialName("dialog_member")
    val dialogMember: UserPreviewResponse?
)
