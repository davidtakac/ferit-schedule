package os.dtakac.feritraspored.calendar.calendarpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.calendar.repository.CalendarRepository

class CalendarPickerViewModel(
        private val calendarRepository: CalendarRepository
) : ViewModel() {
    fun onReadCalendarPermissionGranted() {
        viewModelScope.launch {
            calendarRepository.getAvailableCalendars()
        }
    }
}