package os.dtakac.feritraspored.common.extensions

import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

fun LocalTime.timeFormat() = format(TIME_FORMAT)

fun String.toLocalTime() = LocalTime.parse(this, TIME_FORMAT)