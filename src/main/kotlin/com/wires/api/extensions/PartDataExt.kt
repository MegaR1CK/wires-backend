package com.wires.api.extensions

import com.wires.api.di.getKoinInstance
import io.ktor.http.content.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline fun <reified T> PartData.FormItem.proceedJsonPart(): T? = try {
    val json: Json = getKoinInstance()
    json.decodeFromString<T>(value)
} catch (throwable: Throwable) {
    null
}
