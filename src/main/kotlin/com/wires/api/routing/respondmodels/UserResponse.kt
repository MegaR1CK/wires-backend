package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("avatar")
    val avatar: ImageResponse?,
    @SerializedName("interests")
    val interests: List<String>
)
