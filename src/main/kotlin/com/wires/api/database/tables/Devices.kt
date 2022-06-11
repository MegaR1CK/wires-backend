package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IdTable

object Devices : IdTable<String>() {
    override val id = text("id").entityId()
    val userId = reference("user_id", Users)
    val name = text("name")
    val pushToken = text("push_token").uniqueIndex()
}
