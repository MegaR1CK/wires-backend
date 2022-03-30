package com.wires.api.database.models

import com.wires.api.database.tables.Comments
import com.wires.api.database.tables.Posts
import com.wires.api.extensions.toInstant
import com.wires.api.extensions.toLocalDateTime
import com.wires.api.routing.respondmodels.CommentResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant
import java.time.LocalDateTime

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)
    var author by User referencedOn Comments.userId
    var post by Post referencedOn Posts.id
    var text by Comments.text
    var sendTime: LocalDateTime by Comments.sendTime.transform(
        toColumn = LocalDateTime::toInstant,
        toReal = Instant::toLocalDateTime
    )

    fun toResponse() = CommentResponse(
        id = id.value,
        author = author.toPreviewResponse(),
        text = text,
        sendTime = sendTime.toString()
    )
}
