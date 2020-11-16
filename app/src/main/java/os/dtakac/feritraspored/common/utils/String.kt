package os.dtakac.feritraspored.common.utils

fun String.isWeekNumberInvalid() = isBlank() || isEmpty() || this == "null" || this == "undefined"