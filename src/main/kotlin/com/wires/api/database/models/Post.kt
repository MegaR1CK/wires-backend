package com.wires.api.database.models

import com.wires.api.routing.respondmodels.PostResponse
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
    fun toResponse(userLiked: Boolean) = PostResponse(
        id = id,
        userId = userId,
        text = text,
        imageUrl = imageUrl,
        topic = topic,
        publishTime = publishTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
        isUserLiked = userLiked,
        likesCount = likedUserIds.size,
        commentsCount = commentsCount
    )
}
