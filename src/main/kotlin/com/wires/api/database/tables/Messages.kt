package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Messages : IntIdTable() {
    val userId = reference("user_id", Users)
    val channelId = reference("channel_id", Channels)
    val text = text("text")
    val sendTime = datetime("send_time")
}
