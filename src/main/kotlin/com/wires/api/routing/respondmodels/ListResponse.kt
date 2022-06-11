package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<T>(
    @SerialName("data")
    val data: List<T>
)
