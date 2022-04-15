package com.wires.api.database.entity

import com.wires.api.database.tables.Channels
import com.wires.api.database.tables.ChannelsMembers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ChannelEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChannelEntity>(Channels)
    var name by Channels.name
    var image by ImageEntity optionalReferencedOn Channels.imageUrl
    var members by UserEntity via ChannelsMembers
}
