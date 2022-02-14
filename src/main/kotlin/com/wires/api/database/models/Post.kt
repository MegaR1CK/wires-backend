package com.wires.api.database.models

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
)
