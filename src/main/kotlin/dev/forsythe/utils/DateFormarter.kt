package dev.forsythe.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    .withZone(ZoneId.systemDefault())


/**
 * Formats an Instant to a string: "2025-02-19"
 */
fun Instant.toFormattedDate(): String = DATE_FORMATTER.format(this)

/**
 * Parses a string "2025-02-19" back into an Instant at the start of that day
 */
fun String.toInstantFromDate(): Instant? {
    return try {
        val localDate = LocalDate.parse(this, DATE_FORMATTER)
        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
    } catch (e: DateTimeParseException) {
        null
    }
}
