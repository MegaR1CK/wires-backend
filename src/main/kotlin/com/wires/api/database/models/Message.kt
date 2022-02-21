package com.wires.api.database.models

import com.wires.api.routing.respondmodels.MessageResponse
import com.wires.api.routing.respondmodels.UserPreviewResponse
import java.time.LocalDateTime

data class Message(
    val id: Int,
    val userId: Int,
    val channelId: Int,
    val text: String,
    val sendTime: LocalDateTime
) {
    fun toResponse(author: UserPreviewResponse?, sendTime: String) = MessageResponse(
        id = id,
        author = author,
        text = text,
        sendTime = sendTime
    )
}
