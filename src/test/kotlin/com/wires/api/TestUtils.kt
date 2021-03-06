package com.wires.api

import com.google.gson.GsonBuilder

private const val EMAIL_LENGTH = 7

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getRandomEmail() = "${getRandomString(EMAIL_LENGTH)}@test.com"

inline fun <reified T> T.toJson(): String {
    val gson = GsonBuilder().create()
    return gson.toJson(this)
}

inline fun <reified T> String?.toModel(): T? = this?.let { json ->
    val gson = GsonBuilder().create()
    return@let gson.fromJson(json, T::class.java)
}
