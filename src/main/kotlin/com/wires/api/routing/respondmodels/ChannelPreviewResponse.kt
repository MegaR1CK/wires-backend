package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName
import com.wires.api.model.ChannelType

data class ChannelPreviewResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: ChannelType,
    @SerializedName("image")
    val image: ImageResponse?,
    @SerializedName("last_message")
    val lastMessage: MessageResponse?,
    @SerializedName("unread_messages")
    val unreadMessages: Int,
    @SerializedName("dialog_member")
    val dialogMember: UserPreviewResponse?
)
