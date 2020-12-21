package os.dtakac.feritraspored.calendar.data

abstract class EventData(
        open val id: String
)

data class EventGroupData(
        override val id: String,
        val title: String,
        val isChecked: Boolean
): EventData(id)

data class EventSingleData(
        override val id: String,
        val title: String,
        val description: String,
        val times: String,
        val isChecked: Boolean
): EventData(id)
