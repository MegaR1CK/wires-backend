package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("author")
    val author: UserPreviewResponse?,
    @SerialName("text")
    val text: String,
    @SerialName("send_time")
    val sendTime: String,
    @SerialName("is_initial")
    val isInitial: Boolean,
    @SerialName("is_read")
    val isRead: Boolean
)
