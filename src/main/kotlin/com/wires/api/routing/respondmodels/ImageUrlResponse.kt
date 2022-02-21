package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ImageUrlResponse(
    @SerializedName("image_url")
    val image_url: String?
)
