package os.dtakac.feritraspored.calendar.eventpicker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.calendar.repository.CalendarRepository

class EventPickerViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    fun getEvents(scheduleUrl: String) {
        viewModelScope.launch {
            calendarRepository.getEvents(scheduleUrl).forEach {
                Log.d("caltag", it.toString())
            }
        }
    }
}