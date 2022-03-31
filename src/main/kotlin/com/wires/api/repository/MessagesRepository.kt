package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.MessageEntity
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.database.tables.Messages
import com.wires.api.mappers.ChannelsMapper
import com.wires.api.model.Message
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class MessagesRepository : KoinComponent {

    private val channelsMapper: ChannelsMapper by inject()

    suspend fun getMessages(channelId: Int, limit: Int, offset: Long): List<Message> = dbQuery {
        MessageEntity
            .find { Messages.channelId eq channelId }
            .limit(limit, offset)
            .map(channelsMapper::fromEntityToModel)
    }

    suspend fun getMessageById(messageId: Int): Message? = dbQuery {
        MessageEntity
            .findById(messageId)
            ?.let(channelsMapper::fromEntityToModel)
    }

    suspend fun addMessage(params: MessageInsertParams) = dbQuery {
        Messages.insert { statement ->
            statement[userId] = params.authorId
            statement[channelId] = params.channelId
            statement[text] = params.text
        }[Messages.id].value
    }
}
