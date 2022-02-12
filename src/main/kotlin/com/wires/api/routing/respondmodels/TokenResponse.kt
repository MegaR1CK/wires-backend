package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token")
    val token: String
)
