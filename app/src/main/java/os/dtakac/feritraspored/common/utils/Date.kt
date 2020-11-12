package os.dtakac.feritraspored.common.utils

import java.time.LocalDate

fun LocalDate.isSameWeek(otherDate: LocalDate): Boolean {
    return compareTo(otherDate) in 0..7
}