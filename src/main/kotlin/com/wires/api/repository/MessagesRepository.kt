package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.MessageEntity
import com.wires.api.database.params.MessageInsertParams
import com.wires.api.database.tables.Messages
import com.wires.api.database.tables.MessagesReaders
import com.wires.api.mappers.ChannelsMapper
import com.wires.api.model.Message
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class MessagesRepository : KoinComponent {

    private val channelsMapper: ChannelsMapper by inject()

    suspend fun getMessages(userId: Int, channelId: Int, limit: Int, offset: Long): List<Message> = dbQuery {
        MessageEntity
            .find { Messages.channelId eq channelId }
            .orderBy(Messages.sendTime to SortOrder.DESC)
            .limit(limit, offset)
            .map(channelsMapper::fromEntityToModel)
            .map { message ->
                message.copy(
                    isRead = MessagesReaders.select {
                        (MessagesReaders.userId eq userId) and (MessagesReaders.messageId eq message.id)
                    }.firstOrNull() != null
                )
            }
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
            statement[isInitial] = params.isInitial
        }[Messages.id].value
    }

    suspend fun readMessages(userId: Int, messagesIds: List<Int>) = dbQuery {
        MessagesReaders.batchInsert(messagesIds, shouldReturnGeneratedValues = false, ignore = true) { messageId ->
            this[MessagesReaders.messageId] = messageId
            this[MessagesReaders.userId] = userId
        }
    }

    suspend fun getUnreadMessagesCount(userId: Int, channelId: Int) = dbQuery {
        val messagesInChannel = MessageEntity.find { Messages.channelId eq channelId }
        val readMessagesInChannel = MessageEntity.wrapRows(
            MessagesReaders
                .innerJoin(Messages)
                .select { (Messages.channelId eq channelId) and (MessagesReaders.userId eq userId) }
        )
        (messagesInChannel - readMessagesInChannel).size
    }
}
