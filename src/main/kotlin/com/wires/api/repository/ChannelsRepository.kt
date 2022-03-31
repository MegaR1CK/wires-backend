package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.ChannelEntity
import com.wires.api.database.tables.ChannelsMembers
import com.wires.api.mappers.ChannelsMapper
import com.wires.api.model.Channel
import com.wires.api.model.ChannelPreview
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ChannelsRepository : KoinComponent {

    private val channelsMapper: ChannelsMapper by inject()

    suspend fun getUserChannels(userId: Int): List<ChannelPreview> = dbQuery {
        ChannelEntity
            .find { ChannelsMembers.userId eq userId }
            .map(channelsMapper::fromEntityToPreviewModel)
    }

    suspend fun getChannel(channelId: Int): Channel? = dbQuery {
        ChannelEntity
            .findById(channelId)
            ?.let(channelsMapper::fromEntityToModel)
    }
}
