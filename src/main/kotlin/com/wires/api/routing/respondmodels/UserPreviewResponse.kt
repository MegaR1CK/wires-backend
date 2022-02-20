package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class UserPreviewResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
)
