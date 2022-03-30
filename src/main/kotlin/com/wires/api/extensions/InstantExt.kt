package com.wires.api.extensions

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Instant.toLocalDateTime(): LocalDateTime = atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toInstant(): Instant = atZone(ZoneId.systemDefault()).toInstant()
