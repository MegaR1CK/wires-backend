package com.wires.api.database.tables

import com.wires.api.database.types.array
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.IntegerColumnType

object Channels : IntIdTable() {
    val name = text("name")
    val imageUrl = text("image_url").nullable()
    val membersIds = array<Int>("members_ids", IntegerColumnType()).default(arrayOf())
}
