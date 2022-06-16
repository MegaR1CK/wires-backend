package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.ChannelEntity
import com.wires.api.database.params.ChannelInsertParams
import com.wires.api.database.params.ChannelUpdateParams
import com.wires.api.database.tables.Channels
import com.wires.api.database.tables.ChannelsMembers
import com.wires.api.mappers.ChannelsMapper
import com.wires.api.model.Channel
import com.wires.api.model.ChannelPreview
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ChannelsRepository : KoinComponent {

    private val channelsMapper: ChannelsMapper by inject()

    suspend fun getUserChannels(userId: Int): List<ChannelPreview> = dbQuery {
        ChannelEntity
            .wrapRows(ChannelsMembers.innerJoin(Channels).select { ChannelsMembers.userId eq userId })
            .map { entity -> channelsMapper.fromEntityToPreviewModel(userId, entity) }
    }

    suspend fun getChannel(channelId: Int): Channel? = dbQuery {
        ChannelEntity
            .findById(channelId)
            ?.let(channelsMapper::fromEntityToModel)
    }

    suspend fun createChannel(params: ChannelInsertParams) = dbQuery {
        Channels.insert { statement ->
            statement[name] = params.name
            statement[type] = params.type
            params.imageUrl?.let { statement[imageUrl] = it }
            statement[ownerId] = params.ownerId
        }[Channels.id].value
    }

    suspend fun addUserToChannel(userId: Int, channelId: Int) = dbQuery {
        ChannelsMembers.insert { statement ->
            statement[ChannelsMembers.userId] = userId
            statement[ChannelsMembers.channelId] = channelId
        }
    }

    suspend fun removeUserFromChannel(userId: Int, channelId: Int) = dbQuery {
        ChannelsMembers.deleteWhere { (ChannelsMembers.userId eq userId) and (ChannelsMembers.channelId eq channelId) }
    }

    suspend fun updateChannel(channelId: Int, params: ChannelUpdateParams) = dbQuery {
        Channels.update({ Channels.id eq channelId }) { statement ->
            params.name?.let { statement[name] = it }
            params.imageUrl?.let { statement[imageUrl] = it }
        }
    }
}
