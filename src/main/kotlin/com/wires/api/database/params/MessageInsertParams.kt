package com.wires.api.database.params

data class MessageInsertParams(
    val authorId: Int,
    val channelId: Int,
    val text: String
)
