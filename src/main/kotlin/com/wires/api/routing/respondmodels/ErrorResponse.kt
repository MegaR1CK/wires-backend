package com.wires.api.routing.respondmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String
)

@Serializable
data class ErrorResponseWrapper(
    @SerialName("error")
    val error: ErrorResponse
)
