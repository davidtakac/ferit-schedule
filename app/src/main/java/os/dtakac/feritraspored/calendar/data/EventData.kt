package os.dtakac.feritraspored.calendar.data

import java.time.LocalDate
import java.time.ZonedDateTime

abstract class EventData(
        open val id: String
)

data class EventGroupData(
        override val id: String,
        val date: LocalDate,
): EventData(id)

data class EventSingleData(
        override val id: String,
        val groupId: String,
        val title: String?,
        val description: String?,
        val start: ZonedDateTime,
        val end: ZonedDateTime,
        val location: String?,
        val isChecked: Boolean
): EventData(id)
