package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

data class ObjectResponse<T>(
    @SerializedName("data")
    val data: T
)
