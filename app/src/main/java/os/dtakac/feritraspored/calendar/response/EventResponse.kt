package os.dtakac.feritraspored.calendar.response

import java.time.ZonedDateTime

data class EventResponse(
        val id: String,
        val start: ZonedDateTime,
        val end: ZonedDateTime,
        val title: String?,
        val description: String?,
)