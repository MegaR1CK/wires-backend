package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)

data class ErrorResponseWrapper(
    @SerializedName("error")
    val error: ErrorResponse
)
