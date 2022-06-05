package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Channels : IntIdTable() {
    val name = text("name").nullable()
    val type = text("type")
    val imageUrl = reference("image_url", Images).nullable()
}
