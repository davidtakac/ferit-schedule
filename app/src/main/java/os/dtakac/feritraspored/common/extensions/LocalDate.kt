package os.dtakac.feritraspored.common.extensions

import os.dtakac.feritraspored.common.constants.SCROLL_FORMAT
import os.dtakac.feritraspored.common.constants.URL_FORMAT
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.isSameWeek(otherDate: LocalDate): Boolean {
    //aligns both weeks to monday and then compares them
    return try {
        minusDays(dayOfWeek.value.toLong()) == otherDate.minusDays(otherDate.dayOfWeek.value.toLong())
    } catch (e: Exception) {
        false
    }
}

fun LocalDate.scrollFormat(): String = format(SCROLL_FORMAT)

fun LocalDate.urlFormat(): String = format(URL_FORMAT)