package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("author")
    val author: UserPreviewResponse?,
    @SerialName("text")
    val text: String,
    @SerialName("send_time")
    val sendTime: String
)
