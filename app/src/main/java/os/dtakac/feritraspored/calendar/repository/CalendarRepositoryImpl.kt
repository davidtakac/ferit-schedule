package os.dtakac.feritraspored.calendar.repository

import android.content.ContentResolver
import android.provider.CalendarContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import os.dtakac.feritraspored.calendar.data.CalendarData

class CalendarRepositoryImpl(
        private val contentResolver: ContentResolver
) : CalendarRepository {
    override suspend fun getAvailableCalendars(): List<CalendarData> {
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
        while (cursor?.moveToNext() == true) {
            Log.d("caltag", "calendar id: ${cursor.getString(0)}")
            Log.d("caltag", "calendar name: ${cursor.getString(1)}")
            Log.d("caltag", "account name: ${cursor.getString(2)}")
            Log.d("caltag", "calendar color: ${cursor.getString(3)}")
            Log.d("caltag", "access level: ${cursor.getString(4)}")
            Log.d("caltag", "------------------------------")
        }
        return listOf()
    }
}