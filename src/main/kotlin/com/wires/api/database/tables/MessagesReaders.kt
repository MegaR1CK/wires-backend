package com.wires.api.database.tables

import org.jetbrains.exposed.sql.Table

object MessagesReaders : Table("messages_readers") {
    val messageId = reference("message_id", Messages)
    val userId = reference("user_id", Users)
    override val primaryKey = PrimaryKey(messageId, userId)
}
