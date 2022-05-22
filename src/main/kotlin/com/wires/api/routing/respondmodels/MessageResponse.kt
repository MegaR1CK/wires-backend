package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("author")
    val author: UserPreviewResponse?,
    @SerializedName("text")
    val text: String,
    @SerializedName("send_time")
    val sendTime: String,
    @SerializedName("is_initial")
    val isInitial: Boolean,
    @SerializedName("is_read")
    val isRead: Boolean
)
