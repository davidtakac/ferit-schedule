package os.dtakac.feritraspored.common.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

fun LocalDate.isSameWeek(otherDate: LocalDate): Boolean {
    return get(ChronoField.ALIGNED_WEEK_OF_YEAR) == otherDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
}

fun LocalDate.scrollFormat(): String = format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))

fun LocalDate.urlFormat(): String = format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))