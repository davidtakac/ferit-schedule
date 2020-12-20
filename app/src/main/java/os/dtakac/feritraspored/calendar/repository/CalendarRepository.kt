package os.dtakac.feritraspored.calendar.repository

import os.dtakac.feritraspored.calendar.data.CalendarData

interface CalendarRepository {
    suspend fun getAvailableCalendars(): List<CalendarData>
}