package os.dtakac.feritraspored.calendar.eventpicker.viewmodel

import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.calendar.repository.CalendarRepository

class EventPickerViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    fun getEvents(scheduleUrl: String) {
        // todo: query repository
    }
}