package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName
import com.wires.api.model.ChannelType

data class ChannelCreateParams(
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: ChannelType,
    @SerializedName("members_ids")
    val membersIds: List<Int>
)
