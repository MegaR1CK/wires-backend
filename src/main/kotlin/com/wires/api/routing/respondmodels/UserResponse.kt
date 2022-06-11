package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("avatar")
    val avatar: ImageResponse?,
    @SerialName("interests")
    val interests: List<String>,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?
)
