package os.dtakac.feritraspored.common.extensions

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

fun LocalDate.scrollFormat(): String = format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))

fun LocalDate.urlFormat(): String = format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))