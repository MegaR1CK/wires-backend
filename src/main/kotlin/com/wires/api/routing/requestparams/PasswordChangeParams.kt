package com.wires.api.routing.requestparams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PasswordChangeParams(
    @SerialName("old_password_hash")
    val oldPasswordHash: String,
    @SerialName("new_password_hash")
    val newPasswordHash: String
)
