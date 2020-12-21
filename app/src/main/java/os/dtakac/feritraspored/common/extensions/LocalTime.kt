package os.dtakac.feritraspored.common.extensions

import os.dtakac.feritraspored.common.constants.TIME_PATTERN
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun LocalTime.timeFormat(): String = try {
    format(DateTimeFormatter.ofPattern(TIME_PATTERN))
} catch (e: Exception) {
    ""
}

fun String.toLocalTime(): LocalTime = try {
    LocalTime.parse(this, DateTimeFormatter.ofPattern(TIME_PATTERN))
} catch (e: Exception) {
    LocalTime.of(0, 0)
}