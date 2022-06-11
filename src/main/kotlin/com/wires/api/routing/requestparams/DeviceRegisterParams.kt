package com.wires.api.routing.requestparams

import com.google.gson.annotations.SerializedName

data class DeviceRegisterParams(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("push_token")
    val pushToken: String
)
