package com.wires.api.database.params

import org.jetbrains.exposed.dao.id.EntityID

data class PostUpdateParams(
    val id: Int,
    val text: String?,
    val topic: String?,
    val imageUrl: EntityID<String>?
)
