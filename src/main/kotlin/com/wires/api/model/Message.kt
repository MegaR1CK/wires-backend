package com.wires.api.model

import java.time.LocalDateTime

data class Message(
    val id: Int,
    val author: UserPreview,
    val channelId: Int,
    val text: String,
    val sendTime: LocalDateTime,
    val isInitial: Boolean,
    var isRead: Boolean = false
)
