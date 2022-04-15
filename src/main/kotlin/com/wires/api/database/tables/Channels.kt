package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Channels : IntIdTable() {
    val name = text("name")
    val imageUrl = reference("image_url", Images).nullable()
}
