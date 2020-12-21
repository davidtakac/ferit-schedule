package os.dtakac.feritraspored.calendar.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import os.dtakac.feritraspored.calendar.data.CalendarData
import os.dtakac.feritraspored.calendar.repository.CalendarRepository

class CalendarViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    private lateinit var scheduleUrl: String
    private lateinit var calendarId: String

    val calendars = MutableLiveData<List<CalendarData>>()
    val isCalendarsLoaderVisible = MutableLiveData<Boolean>()

    fun initialize(scheduleUrl: String) {
        this.scheduleUrl = scheduleUrl
    }

    fun setCalendarId(calendarId: String) {
        this.calendarId = calendarId
    }

    fun getCalendars() {
        if (calendars.value == null) {
            viewModelScope.launch {
                isCalendarsLoaderVisible.value = true

                val response = calendarRepository.getAvailableCalendars()
                val calendarData = withContext(Dispatchers.Default) {
                    response.map {
                        CalendarData(
                                id = it.id,
                                name = if (it.name == it.account) null else it.name,
                                account = it.account,
                                color = it.color.toIntOrNull()
                        )
                    }
                }

                calendars.value = calendarData
                isCalendarsLoaderVisible.value = false
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch {
            calendarRepository.getEvents(scheduleUrl).forEach {
                Log.d("caltag", it.toString())
            }
        }
    }
}