package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

// TODO: time as string
data class MessageResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("author")
    val author: UserPreviewResponse?,
    @SerializedName("text")
    val text: String,
    @SerializedName("send_time")
    val sendTime: Long
)