package os.dtakac.feritraspored.common.extensions

import os.dtakac.feritraspored.common.constants.TIME_FORMAT
import java.time.LocalTime

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