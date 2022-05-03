package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Messages : IntIdTable() {
    val userId = reference("user_id", Users)
    val channelId = reference("channel_id", Channels)
    val text = text("text")
    val sendTime = timestamp("send_time").defaultExpression(CurrentTimestamp())
    val isInitial = bool("is_initial")
}
