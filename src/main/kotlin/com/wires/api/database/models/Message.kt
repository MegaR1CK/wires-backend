package com.wires.api.database.models

import com.wires.api.database.tables.Messages
import com.wires.api.extensions.toInstant
import com.wires.api.extensions.toLocalDateTime
import com.wires.api.routing.respondmodels.MessageResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant
import java.time.LocalDateTime

class Message(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Message>(Messages)
    var author by User referencedOn Messages.userId
    var channel by Channel referencedOn Messages.channelId
    var text by Messages.text
    var sendTime: LocalDateTime by Messages.sendTime.transform(
        toColumn = LocalDateTime::toInstant,
        toReal = Instant::toLocalDateTime
    )

    fun toResponse() = MessageResponse(
        id = id.value,
        author = author.toPreviewResponse(),
        text = text,
        sendTime = sendTime.toString()
    )
}
