package com.wires.api.mappers

import com.wires.api.database.entity.ChannelEntity
import com.wires.api.database.entity.MessageEntity
import com.wires.api.extensions.toLocalDateTime
import com.wires.api.model.Channel
import com.wires.api.model.ChannelPreview
import com.wires.api.model.Message
import com.wires.api.routing.respondmodels.ChannelPreviewResponse
import com.wires.api.routing.respondmodels.ChannelResponse
import com.wires.api.routing.respondmodels.MessageResponse
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ChannelsMapper : KoinComponent {

    private val userMapper: UserMapper by inject()

    fun fromEntityToModel(channelEntity: ChannelEntity) = Channel(
        id = channelEntity.id.value,
        name = channelEntity.name,
        imageUrl = channelEntity.imageUrl,
        members = channelEntity.members.map { userMapper.fromEntityToPreviewModel(it) }
    )

    fun fromEntityToPreviewModel(channelEntity: ChannelEntity) = ChannelPreview(
        id = channelEntity.id.value,
        name = channelEntity.name,
        imageUrl = channelEntity.imageUrl
    )

    fun fromEntityToModel(messageEntity: MessageEntity) = Message(
        id = messageEntity.id.value,
        author = userMapper.fromEntityToModel(messageEntity.author),
        channelId = messageEntity.channel.id.value,
        text = messageEntity.text,
        sendTime = messageEntity.sendTime.toLocalDateTime()
    )

    fun fromModelToResponse(channel: Channel) = ChannelResponse(
        id = channel.id,
        name = channel.name,
        imageUrl = channel.imageUrl,
        members = channel.members.map { userMapper.fromModelToResponse(it) }
    )

    fun fromModelToResponse(channelPreview: ChannelPreview) = ChannelPreviewResponse(
        id = channelPreview.id,
        name = channelPreview.name,
        imageUrl = channelPreview.imageUrl
    )

    fun fromModelToResponse(message: Message) = MessageResponse(
        id = message.id,
        author = userMapper.fromModelToResponse(message.author),
        text = message.text,
        sendTime = message.sendTime.toString()
    )
}
