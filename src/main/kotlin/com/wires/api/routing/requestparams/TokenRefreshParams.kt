package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshParams(
    @SerialName("refresh_token")
    val refreshToken: String
)
