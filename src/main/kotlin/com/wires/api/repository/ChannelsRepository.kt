package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.tables.Channels
import com.wires.api.extensions.toChannel
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single

@Single
class ChannelsRepository {

    suspend fun getUserChannels(userChannels: List<Int>) = dbQuery {
        Channels.select { Channels.id.inList(userChannels) }.mapNotNull { it.toChannel() }
    }

    suspend fun getChannel(channelId: Int) = dbQuery {
        Channels.select { Channels.id.eq(channelId) }.map { it.toChannel() }.singleOrNull()
    }
}
