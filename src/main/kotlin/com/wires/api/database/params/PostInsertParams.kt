package com.wires.api.database.params

import org.jetbrains.exposed.dao.id.EntityID

data class PostInsertParams(
    val text: String,
    val imageUrl: EntityID<String>?,
    val topic: String,
    val userId: Int
)
