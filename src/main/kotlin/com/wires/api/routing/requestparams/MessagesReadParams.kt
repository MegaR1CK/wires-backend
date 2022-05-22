package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class MessagesReadParams(
    @SerializedName("messages_ids")
    val messagesIds: List<Int>
)
