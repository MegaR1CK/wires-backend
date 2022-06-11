package com.wires.api.database.params

data class DeviceInsertParams(
    val id: String,
    val userId: Int,
    val name: String,
    val pushToken: String
)
