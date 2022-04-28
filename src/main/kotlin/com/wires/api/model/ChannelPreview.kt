package com.wires.api.model

data class ChannelPreview(
    val id: Int,
    val name: String,
    val type: String,
    val image: Image?,
    var lastMessage: Message?
)
