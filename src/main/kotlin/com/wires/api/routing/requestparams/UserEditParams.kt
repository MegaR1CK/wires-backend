package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class UserEditParams(
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("password_hash")
    val passwordHash: String? = null,
    @SerializedName("interests")
    val interests: List<String>? = null
)
