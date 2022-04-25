package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class PasswordChangeParams(
    @SerializedName("old_password_hash")
    val oldPasswordHash: String,
    @SerializedName("new_password_hash")
    val newPasswordHash: String
)
