package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class PostCreateParams(
    @SerializedName("text")
    val text: String,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("image_url")
    val imageUrl: String?
)
