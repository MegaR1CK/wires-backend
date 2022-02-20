package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("author")
    val author: UserPreviewResponse?,
    @SerializedName("text")
    val text: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("publish_time")
    val publishTime: Long,
    @SerializedName("is_user_liked")
    val isUserLiked: Boolean,
    @SerializedName("likes_count")
    val likesCount: Int,
    @SerializedName("comments_count")
    val commentsCount: Int
)
