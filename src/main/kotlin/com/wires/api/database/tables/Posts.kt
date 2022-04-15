package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Posts : IntIdTable() {
    val userId = reference("user_id", Users)
    val text = text("text")
    val imageUrl = reference("image_url", Images).nullable()
    val topic = text("topic")
    val publishTime = timestamp("publish_time").defaultExpression(CurrentTimestamp())
    val likedUserIds = text("liked_user_ids").nullable()
    val commentsCount = integer("comments_count").default(0)
}
