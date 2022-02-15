package com.wires.api.database.params

data class PostInsertParams(
    val text: String,
    val imageUrl: String?,
    val topic: String,
    val userId: Int
)
