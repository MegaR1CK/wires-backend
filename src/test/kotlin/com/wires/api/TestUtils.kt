package com.wires.api

import com.google.gson.GsonBuilder

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

inline fun <reified T> T.toJson(): String {
    val gson = GsonBuilder().create()
    return gson.toJson(this)
}

inline fun <reified T> String?.toModel(): T? = this?.let { json ->
    val gson = GsonBuilder().create()
    return@let gson.fromJson(json, T::class.java)
}
