package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class LoginUserParams(
    @SerializedName("email")
    val email: String,
    @SerializedName("password_hash")
    val passwordHash: String
)
