package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class MessageSendParams(
    @SerializedName("text")
    val text: String
)