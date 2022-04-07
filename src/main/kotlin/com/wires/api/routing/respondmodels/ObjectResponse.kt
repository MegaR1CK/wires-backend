package com.wires.api.routing.respondmodels

import com.google.gson.annotations.SerializedName

open class ObjectResponse<out T>(
    @SerializedName("data")
    val data: T
)
