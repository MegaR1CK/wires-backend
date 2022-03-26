package com.wires.api.extensions

import com.google.gson.Gson
import com.wires.api.di.inject
import io.ktor.http.content.*

inline fun <reified T> PartData.FormItem.proceedJsonPart(): T? = try {
    val gson: Gson by inject()
    gson.fromJson(value, T::class.java)
} catch (throwable: Throwable) {
    null
}
