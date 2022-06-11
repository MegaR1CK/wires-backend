package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ObjectResponse<out T>(
    @SerialName("data")
    val data: T
)
