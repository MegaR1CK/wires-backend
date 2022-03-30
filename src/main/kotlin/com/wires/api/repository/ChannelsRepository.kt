package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.Channel
import com.wires.api.database.tables.ChannelsMembers
import org.koin.core.annotation.Single

@Single
class ChannelsRepository {

    suspend fun getUserChannels(userId: Int) = dbQuery {
        Channel.find { ChannelsMembers.userId eq userId }
    }

    suspend fun getChannel(channelId: Int) = dbQuery {
        Channel.findById(channelId)
    }
}
