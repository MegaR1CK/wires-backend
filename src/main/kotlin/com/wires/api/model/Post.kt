package com.wires.api.model

import java.time.LocalDateTime

data class Post(
    val id: Int,
    val author: UserPreview,
    val text: String,
    val imageUrl: String?,
    val topic: String,
    val publishTime: LocalDateTime,
    val likedUserIds: List<Int>,
    val commentsCount: Int
)
