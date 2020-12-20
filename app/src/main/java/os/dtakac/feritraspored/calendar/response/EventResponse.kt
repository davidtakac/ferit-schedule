package os.dtakac.feritraspored.calendar.response

data class EventResponse(
        val start: Long,
        val end: Long,
        val title: String?,
        val description: String?,
)