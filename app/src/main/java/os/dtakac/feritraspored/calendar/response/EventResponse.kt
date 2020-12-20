package os.dtakac.feritraspored.calendar.response

import java.time.LocalDateTime

data class EventResponse(
        val id: Int,
        val start: LocalDateTime,
        val end: LocalDateTime,
        val title: String?,
        val description: String?,
)