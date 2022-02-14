package com.wires.api.extensions

private const val SEPARATOR = ","

fun String?.toIntArray() = this?.split(SEPARATOR)?.toList()?.map { it.toInt() } ?: listOf()

fun IntArray.toSeparatedString() = if (isEmpty()) null else joinToString(SEPARATOR)
