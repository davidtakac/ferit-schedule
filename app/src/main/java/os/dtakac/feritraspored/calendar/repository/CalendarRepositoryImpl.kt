package os.dtakac.feritraspored.calendar.repository

import android.content.ContentResolver
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import os.dtakac.feritraspored.calendar.response.CalendarResponse

class CalendarRepositoryImpl(
        private val contentResolver: ContentResolver
) : CalendarRepository {
    override suspend fun getAvailableCalendars(): List<CalendarResponse> {
        // query content provider for writeable calendars
        val eventProjection: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
        )
        val selection = "((${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?))"
        val args = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())
        val cursor = withContext(Dispatchers.IO) {
            contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    eventProjection,
                    selection,
                    args,
                    null
            )
        }
        // map to calendar data
        val calendars = mutableListOf<CalendarResponse>()
        withContext(Dispatchers.IO) {
            cursor?.use {
                while (it.moveToNext()) {
                    calendars.add(CalendarResponse(
                            id = cursor.getString(0),
                            name = cursor.getString(1),
                            account = cursor.getString(2),
                            color = cursor.getString(3)
                    ))
                }
            }
        }
        return calendars
    }
}