package com.wires.api.database.tables

import com.wires.api.database.types.array
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.javatime.datetime

object Posts : IntIdTable() {
    val userId = reference("user_id", Users)
    val text = text("text")
    val imageUrl = text("image_url").nullable()
    val publishTime = datetime("publish_time")
    val likedUserIds = array<Int>("liked_user_ids", IntegerColumnType()).default(arrayOf())
}
