package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class TokenRefreshParams(
    @SerializedName("refresh_token")
    val refreshToken: String
)
