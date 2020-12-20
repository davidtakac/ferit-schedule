package os.dtakac.feritraspored.calendar.calendarpicker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import os.dtakac.feritraspored.calendar.data.CalendarData
import os.dtakac.feritraspored.calendar.repository.CalendarRepository

class CalendarPickerViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    val calendars = MutableLiveData<List<CalendarData>>()
    val isLoaderVisible = MutableLiveData<Boolean>()

    fun getCalendars() {
        viewModelScope.launch {
            isLoaderVisible.value = true

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
            isLoaderVisible.value = false
        }
    }
}