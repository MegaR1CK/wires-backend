package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.tables.Messages
import com.wires.api.extensions.toMessage
import org.jetbrains.exposed.sql.select

class MessagesRepository {

    suspend fun getMessages(channelId: Int) = dbQuery {
        Messages.select { Messages.channelId.eq(channelId) }.mapNotNull { it.toMessage() }
    }
}
