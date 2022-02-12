package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class EditUserParams(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password_hash")
    val passwordHash: String
)
