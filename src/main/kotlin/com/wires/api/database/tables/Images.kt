package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IdTable

object Images : IdTable<String>() {
    override val id = text("url").entityId().uniqueIndex()
    val width = integer("width")
    val height = integer("height")
}
