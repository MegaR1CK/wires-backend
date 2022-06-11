package com.wires.api.routing.requestparams

import com.wires.api.model.ChannelType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelCreateParams(
    @SerialName("name")
    val name: String?,
    @SerialName("type")
    val type: ChannelType,
    @SerialName("members_ids")
    val membersIds: List<Int>
)
