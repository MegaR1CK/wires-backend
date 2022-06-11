package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreviewResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("username")
    val username: String,
    @SerialName("avatar")
    val avatar: ImageResponse?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?
)
