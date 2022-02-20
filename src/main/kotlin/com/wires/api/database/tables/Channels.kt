package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Channels : IntIdTable() {
    val name = text("name")
    val imageUrl = text("image_url").nullable()
    val membersIds = text("members_ids").nullable()
}
