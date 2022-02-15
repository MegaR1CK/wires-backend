package com.wires.api.database.params

data class CommentInsertParams(
    val userId: Int,
    val postId: Int,
    val text: String
)
