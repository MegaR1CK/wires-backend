package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.database.tables.Messages
import com.wires.api.extensions.toMessage
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class MessagesRepository {

    suspend fun getMessages(channelId: Int) = dbQuery {
        Messages.select { Messages.channelId.eq(channelId) }.mapNotNull { it.toMessage() }
    }

    suspend fun addMessage(params: MessageInsertParams) = dbQuery {
        Messages.insert { statement ->
            statement[userId] = params.authorId
            statement[channelId] = params.channelId
            statement[text] = params.text
        }
    }
}
