package com.wires.api.database.params

data class PostInsertParams(
    val text: String,
    val imageUrl: String?,
    val userId: Int
)
