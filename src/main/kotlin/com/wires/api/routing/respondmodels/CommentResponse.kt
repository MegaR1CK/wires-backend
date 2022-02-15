package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("author")
    val author: UserResponse?,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("send_time")
    val sendTime: Long
)
