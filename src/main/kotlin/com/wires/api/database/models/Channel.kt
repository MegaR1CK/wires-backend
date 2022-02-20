package com.wires.api.database.models

import com.wires.api.routing.respondmodels.ChannelPreviewResponse
import com.wires.api.routing.respondmodels.ChannelResponse
import com.wires.api.routing.respondmodels.UserPreviewResponse

data class Channel(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val membersIds: List<Int>
) {
    fun toPreviewResponse() = ChannelPreviewResponse(
        id = id,
        name = name,
        imageUrl = imageUrl
    )

    fun toResponse(members: List<UserPreviewResponse>) = ChannelResponse(
        id = id,
        name = name,
        imageUrl = imageUrl,
        members = members
    )
}
