package com.wires.api.database.models

import com.wires.api.routing.respondmodels.PostResponse
import com.wires.api.routing.respondmodels.UserPreviewResponse
import java.time.LocalDateTime

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
    fun toResponse(author: UserPreviewResponse?, publishTime: String) = PostResponse(
        id = id,
        author = author,
        text = text,
        imageUrl = imageUrl,
        topic = topic,
        publishTime = publishTime,
        isUserLiked = likedUserIds.contains(id),
        likesCount = likedUserIds.size,
        commentsCount = commentsCount
    )
}
