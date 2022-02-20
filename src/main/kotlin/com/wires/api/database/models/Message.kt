package com.wires.api.database.models

import com.wires.api.routing.respondmodels.MessageResponse
import com.wires.api.routing.respondmodels.UserPreviewResponse
import java.time.LocalDateTime
import java.time.ZoneId

data class Message(
    val id: Int,
    val userId: Int,
    val channelId: Int,
    val text: String,
    val sendTime: LocalDateTime
) {
    fun toResponse(author: UserPreviewResponse?) = MessageResponse(
        id = id,
        author = author,
        text = text,
        sendTime = sendTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    )
}
