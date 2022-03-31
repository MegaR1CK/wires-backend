package com.wires.api.database.entity

import com.wires.api.database.tables.Messages
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MessageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MessageEntity>(Messages)
    var author by UserEntity referencedOn Messages.userId
    var channel by ChannelEntity referencedOn Messages.channelId
    var text by Messages.text
    var sendTime by Messages.sendTime
}
