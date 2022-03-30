package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.Message
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.database.tables.Messages
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single

@Single
class MessagesRepository {

    suspend fun getMessages(channelId: Int, limit: Int, offset: Long) = dbQuery {
        Message
            .find { Messages.channelId eq channelId }
            .limit(limit, offset)
    }

    suspend fun getMessageById(messageId: Int) = dbQuery {
        Message.findById(messageId)
    }

    suspend fun addMessage(params: MessageInsertParams) = dbQuery {
        Messages.insert { statement ->
            statement[userId] = params.authorId
            statement[channelId] = params.channelId
            statement[text] = params.text
        }[Messages.id].value
    }
}
