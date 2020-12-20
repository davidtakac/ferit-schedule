package os.dtakac.feritraspored.calendar.repository

import os.dtakac.feritraspored.calendar.response.CalendarResponse

interface CalendarRepository {
    suspend fun getAvailableCalendars(): List<CalendarResponse>
    suspend fun getEvents(scheduleUrl: String): List<EventResponse>
}