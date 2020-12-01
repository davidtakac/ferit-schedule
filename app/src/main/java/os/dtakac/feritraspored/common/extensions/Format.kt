package os.dtakac.feritraspored.common.extensions

fun formatTime(hour: Int, minute: Int): String {
    val hourStr = (if (hour < 10) "0" else "") + hour
    val minStr = (if (minute < 10) "0" else "") + minute
    return "$hourStr:$minStr"
}