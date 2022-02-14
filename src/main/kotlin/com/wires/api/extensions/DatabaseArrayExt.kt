package com.wires.api.extensions

private const val SEPARATOR = ","

fun String?.toIntArray() = this?.split(SEPARATOR)?.toList()?.map { it.toInt() } ?: listOf()

fun String?.toStringArray() = this?.split(SEPARATOR)?.toList() ?: listOf()

fun <T> Array<T>.toSeparatedString() = if (isEmpty()) null else joinToString(SEPARATOR)
