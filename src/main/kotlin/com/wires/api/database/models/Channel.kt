package com.wires.api.database.models

import com.wires.api.database.tables.Channels
import com.wires.api.database.tables.ChannelsMembers
import com.wires.api.routing.respondmodels.ChannelPreviewResponse
import com.wires.api.routing.respondmodels.ChannelResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Channel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Channel>(Channels)
    var name by Channels.name
    var imageUrl by Channels.imageUrl
    var members by User via ChannelsMembers

    fun toPreviewResponse() = ChannelPreviewResponse(
        id = id.value,
        name = name,
        imageUrl = imageUrl
    )

    fun toResponse() = ChannelResponse(
        id = id.value,
        name = name,
        imageUrl = imageUrl,
        members = members.map { it.toPreviewResponse() }
    )

    fun containsUser(userId: Int) = members.map { it.id.value }.contains(userId)
}
