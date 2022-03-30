package com.wires.api.extensions

private const val SEPARATOR = ","

fun String?.toIntList() = this?.split(SEPARATOR)?.toList()?.map { it.toInt() } ?: listOf()

fun String?.toStringList() = this?.split(SEPARATOR)?.toList() ?: listOf()

fun <T> List<T>.toSeparatedString() = if (isEmpty()) null else joinToString(SEPARATOR)
