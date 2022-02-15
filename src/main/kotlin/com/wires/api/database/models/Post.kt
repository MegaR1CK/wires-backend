package com.wires.api.database.models

import com.wires.api.routing.respondmodels.PostResponse
import com.wires.api.routing.respondmodels.UserResponse
import java.time.LocalDateTime
import java.time.ZoneId

data class Post(
    val id: Int,
    val userId: Int,
    val text: String,
    val imageUrl: String?,
    val topic: String,
    val publishTime: LocalDateTime,
    val likedUserIds: List<Int>,
    val commentsCount: Int
) {
    fun toResponse(author: UserResponse?) = PostResponse(
        id = id,
        author = author,
        text = text,
        imageUrl = imageUrl,
        topic = topic,
        publishTime = publishTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
        isUserLiked = likedUserIds.contains(id),
        likesCount = likedUserIds.size,
        commentsCount = commentsCount
    )
}
