package com.wires.api.routing.respondmodels

data class PostResponse(
    val id: Int,
    val userId: Int,
    val text: String,
    val imageUrl: String?,
    val topic: String,
    val publishTime: Long,
    val isUserLiked: Boolean,
    val likesCount: Int,
    val commentsCount: Int
)
