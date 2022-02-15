package com.wires.api.database.models

import com.wires.api.routing.respondmodels.CommentResponse
import com.wires.api.routing.respondmodels.UserResponse
import java.time.LocalDateTime
import java.time.ZoneId

data class Comment(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val text: String,
    val sendTime: LocalDateTime
) {
    fun toResponse(author: UserResponse?) = CommentResponse(
        id = id,
        author = author,
        postId = postId,
        text = text,
        sendTime = sendTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    )
}
