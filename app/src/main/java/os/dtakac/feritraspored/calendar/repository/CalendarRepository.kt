package os.dtakac.feritraspored.calendar.repository

import os.dtakac.feritraspored.calendar.response.CalendarResponse
import os.dtakac.feritraspored.calendar.response.EventResponse

interface CalendarRepository {
    suspend fun getAvailableCalendars(): List<CalendarResponse>
    suspend fun getEvents(scheduleUrl: String): List<EventResponse>
}