package com.wires.api.database.tables

import org.jetbrains.exposed.sql.Table

object ChannelsMembers : Table() {
    val channelId = reference("channel_id", Channels)
    val userId = reference("user_id", Users)
    override val primaryKey = PrimaryKey(channelId, userId)
}
