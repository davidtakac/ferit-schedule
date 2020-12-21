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

class CalendarViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    private lateinit var scheduleUrl: String
    private lateinit var calendarId: String
    private lateinit var eventResponse: List<EventResponse>

    val calendarData = MutableLiveData<List<CalendarData>>()
    val eventData = MutableLiveData<List<EventData>>()

    val isCalendarsLoaderVisible = MutableLiveData<Boolean>()
    val isEventsLoaderVisible = MutableLiveData<Boolean>()

    fun initialize(scheduleUrl: String) {
        this.scheduleUrl = scheduleUrl
    }

    fun onCalendarPicked(calendarId: String) {
        this.calendarId = calendarId
        // todo: display dialog to user, ask for permissions etc
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
        if (eventData.value == null) {
            viewModelScope.launch {
                isEventsLoaderVisible.value = true

                val response = calendarRepository.getEvents(scheduleUrl)
                eventResponse = response
                eventData.value = createEventData(response)

                isEventsLoaderVisible.value = false
            }
        }
    }

    fun onEventChecked(data: EventSingleData, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedEventData = mutableListOf<EventData>()
            withContext(Dispatchers.Default) {
                updatedEventData.addAll(eventData.value ?: listOf())
                updatedEventData.forEachIndexed { index, it ->
                    if (it is EventSingleData && it.id == data.id) {
                        updatedEventData[index] = it.copy(isChecked = isChecked)
                    }
                }
            }
            eventData.value = updatedEventData
        }
    }

    fun onEventGroupChecked(data: EventGroupData, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedEventData = mutableListOf<EventData>()
            withContext(Dispatchers.IO) {
                updatedEventData.addAll(eventData.value ?: listOf())
                updatedEventData.forEachIndexed { index, it ->
                    if (it is EventSingleData && it.groupId == data.id) {
                        updatedEventData[index] = it.copy(isChecked = isChecked)
                    }
                }
            }
            eventData.value = updatedEventData
        }
    }

    private suspend fun createEventData(events: List<EventResponse>): List<EventData> {
        val results = mutableListOf<EventData>()
        withContext(Dispatchers.Default) {
            val eventsToDates = events.groupBy { it.start.toLocalDate() }
            eventsToDates.keys.forEachIndexed { idx, date ->
                val groupId = "group$idx"
                val eventGroup = EventGroupData(
                        id = groupId,
                        date = date
                )
                results.add(eventGroup)
                eventsToDates[date]?.forEach {
                    val event = EventSingleData(
                            id = it.id,
                            groupId = groupId,
                            title = it.title,
                            description = it.description,
                            start = it.start,
                            end = it.end,
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