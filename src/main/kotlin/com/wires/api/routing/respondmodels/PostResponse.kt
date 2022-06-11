package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("author")
    val author: UserPreviewResponse?,
    @SerialName("text")
    val text: String,
    @SerialName("image")
    val image: ImageResponse?,
    @SerialName("topic")
    val topic: String,
    @SerialName("publish_time")
    val publishTime: String,
    @SerialName("is_user_liked")
    val isUserLiked: Boolean,
    @SerialName("likes_count")
    val likesCount: Int,
    @SerialName("comments_count")
    val commentsCount: Int
)
