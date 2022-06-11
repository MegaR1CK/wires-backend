package com.wires.api.extensions

import io.ktor.http.content.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline fun <reified T> PartData.FormItem.proceedJsonPart(): T? = try {
    Json.decodeFromString<T>(value)
} catch (throwable: Throwable) {
    null
}
