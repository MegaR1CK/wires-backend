package com.wires.api.database.params

import org.jetbrains.exposed.dao.id.EntityID

data class ChannelUpdateParams(
    val name: String? = null,
    val imageUrl: EntityID<String>? = null
)
