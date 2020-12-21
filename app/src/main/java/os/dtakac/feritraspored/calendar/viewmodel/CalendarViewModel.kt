package os.dtakac.feritraspored.calendar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import os.dtakac.feritraspored.calendar.data.CalendarData
import os.dtakac.feritraspored.calendar.data.EventData
import os.dtakac.feritraspored.calendar.data.EventGroupData
import os.dtakac.feritraspored.calendar.data.EventSingleData
import os.dtakac.feritraspored.calendar.repository.CalendarRepository
import os.dtakac.feritraspored.calendar.response.CalendarResponse
import os.dtakac.feritraspored.calendar.response.EventResponse
import os.dtakac.feritraspored.common.extensions.scrollFormat
import os.dtakac.feritraspored.common.extensions.timeFormat

class CalendarViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    private lateinit var scheduleUrl: String
    private lateinit var calendarId: String

    val calendarData = MutableLiveData<List<CalendarData>>()
    val events = MutableLiveData<List<EventData>>()

    val isCalendarsLoaderVisible = MutableLiveData<Boolean>()
    val isEventsLoaderVisible = MutableLiveData<Boolean>()

    fun initialize(scheduleUrl: String) {
        this.scheduleUrl = scheduleUrl
    }

    fun setCalendarId(calendarId: String) {
        this.calendarId = calendarId
    }

    fun getCalendars() {
        if (calendarData.value == null) {
            viewModelScope.launch {
                isCalendarsLoaderVisible.value = true

                val response = calendarRepository.getAvailableCalendars()
                calendarData.value = createCalendarData(response)

                isCalendarsLoaderVisible.value = false
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch {
            isEventsLoaderVisible.value = true

            val response = calendarRepository.getEvents(scheduleUrl)
            events.value = createEventData(response)

            isEventsLoaderVisible.value = false
        }
    }

    private suspend fun createEventData(events: List<EventResponse>): List<EventData> {
        val results = mutableListOf<EventData>()
        withContext(Dispatchers.Default) {
            val eventsToDates = events.groupBy { it.start.toLocalDate() }
            eventsToDates.keys.forEachIndexed { idx, date ->
                val eventGroup = EventGroupData(
                        id = "group$idx",
                        title = date.scrollFormat(),
                        isChecked = true)
                results.add(eventGroup)
                eventsToDates[date]?.forEach {
                    val event = EventSingleData(
                            id = it.id,
                            title = it.title ?: "",
                            description = it.description ?: "",
                            times = "${it.start.toLocalTime().timeFormat()} - ${it.end.toLocalTime().timeFormat()}",
                            isChecked = true
                    )
                    results.add(event)
                }
            }
        }
        return results
    }

    private suspend fun createCalendarData(response: List<CalendarResponse>): List<CalendarData> {
        return withContext(Dispatchers.Default) {
            response.map {
                CalendarData(
                        id = it.id,
                        name = if (it.name == it.account) null else it.name,
                        account = it.account,
                        color = it.color.toIntOrNull()
                )
            }
        }
    }
}