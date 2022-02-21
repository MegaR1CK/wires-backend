package com.wires.api.database.models

import com.wires.api.routing.respondmodels.CommentResponse
import com.wires.api.routing.respondmodels.UserPreviewResponse
import java.time.LocalDateTime

data class Comment(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val text: String,
    val sendTime: LocalDateTime
) {
    fun toResponse(author: UserPreviewResponse?, sendTime: String) = CommentResponse(
        id = id,
        author = author,
        text = text,
        sendTime = sendTime
    )
}
