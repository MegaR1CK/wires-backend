package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class PostEditParams(
    @SerializedName("text")
    val text: String?,
    @SerializedName("topic")
    val topic: String?
)
