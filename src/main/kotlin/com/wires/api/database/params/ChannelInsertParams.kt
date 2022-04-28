package com.wires.api.database.params

import org.jetbrains.exposed.dao.id.EntityID

data class ChannelInsertParams(
    val name: String,
    val type: String,
    val imageUrl: EntityID<String>?
)
