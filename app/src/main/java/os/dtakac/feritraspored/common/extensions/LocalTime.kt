package os.dtakac.feritraspored.common.extensions

import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

fun LocalTime.timeFormat(): String = try {
    format(TIME_FORMAT)
} catch (e: Exception) {
    ""
}

fun String.toLocalTime(): LocalTime = try {
    LocalTime.parse(this, TIME_FORMAT)
} catch (e: Exception) {
    LocalTime.of(0, 0)
}