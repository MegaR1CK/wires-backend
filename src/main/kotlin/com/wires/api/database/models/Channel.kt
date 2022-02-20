package com.wires.api.database.models

import com.wires.api.routing.respondmodels.ChannelPreviewResponse

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
}
