package com.wires.api.extensions

import com.google.gson.GsonBuilder
import io.ktor.http.content.*

inline fun <reified T> PartData.FormItem.proceedJsonPart(): T? = try {
    GsonBuilder().create().fromJson(value, T::class.java)
} catch (throwable: Throwable) {
    null
}
