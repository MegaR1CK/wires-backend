package com.wires.api.database.params

data class PostUpdateParams(
    val id: Int,
    val text: String?,
    val topic: String?,
    val imageUrl: String?
)
