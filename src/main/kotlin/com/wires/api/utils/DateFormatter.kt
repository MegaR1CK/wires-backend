package com.wires.api.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateFormatter {

    companion object {
        private const val PATTERN_FULL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss"
    }

    private val fullDateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_FULL_DATE_TIME, Locale.getDefault())

    fun dateTimeToFullString(dateTime: LocalDateTime): String {
        return fullDateTimeFormatter.format(dateTime)
    }
}
