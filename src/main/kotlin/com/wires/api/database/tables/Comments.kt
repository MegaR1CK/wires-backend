package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Comments : IntIdTable() {
    val userId = reference("user_id", Users)
    val postId = reference("post_id", Posts)
    val text = text("text")
    val sendTime = datetime("send_time")
}
