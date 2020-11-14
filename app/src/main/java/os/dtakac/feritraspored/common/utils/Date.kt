package os.dtakac.feritraspored.common.utils

import java.time.LocalDate
import java.time.temporal.ChronoField

fun LocalDate.isSameWeek(otherDate: LocalDate): Boolean {
    return get(ChronoField.ALIGNED_WEEK_OF_YEAR) == otherDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
}