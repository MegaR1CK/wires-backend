package com.wires.api.model

import java.time.LocalDateTime

data class Comment(
    val id: Int,
    val author: UserPreview,
    val postId: Int,
    val text: String,
    val sendTime: LocalDateTime
)
