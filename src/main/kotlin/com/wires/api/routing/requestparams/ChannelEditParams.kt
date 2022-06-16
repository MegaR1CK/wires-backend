package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelEditParams(
    @SerialName("name")
    val name: String,
    @SerialName("members_ids")
    val membersIds: List<Int>
)
