package com.wires.api.model

data class ChannelPreview(
    val id: Int,
    val name: String?,
    val type: ChannelType,
    val image: Image?,
    var lastMessage: Message? = null,
    var unreadMessages: Int = 0,
    var dialogMember: UserPreview? = null
)
