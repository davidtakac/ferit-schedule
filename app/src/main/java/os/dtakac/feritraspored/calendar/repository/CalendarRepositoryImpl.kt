package os.dtakac.feritraspored.calendar.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import os.dtakac.feritraspored.calendar.response.CalendarResponse
import os.dtakac.feritraspored.calendar.response.EventResponse
import os.dtakac.feritraspored.common.constants.CALENDAR_DATETIME_FORMAT
import java.lang.Exception
import java.time.LocalDateTime

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

    override suspend fun getEvents(scheduleUrl: String): List<EventResponse> {
        // fetch document
        val document = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Jsoup.connect(scheduleUrl).get()
        }
        // select all google calendar links
        val uris = withContext(Dispatchers.Default) {
            document.select(".hide a[href*=calendar]")
                    .eachAttr("href")
                    .map { Uri.parse(it) }
        }
        // extract their info and map to response
        val events = mutableListOf<EventResponse>()
        withContext(Dispatchers.Default) {
            for (i in uris.indices) {
                val uri = uris[i]
                val dates = uri.getQueryParameter("dates")?.split("/")
                val start = parseDate(dates?.getOrNull(0))
                val end = parseDate(dates?.getOrNull(1))
                if (start == null || end == null) {
                    continue
                }
                val title = uri.getQueryParameter("text")
                val description = uri.getQueryParameter("details")

                events.add(EventResponse(
                        id = i,
                        start = start,
                        end = end,
                        title = title,
                        description = description,
                ))
            }
        }
        return events
    }

    private fun parseDate(dateFromUrl: String?): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateFromUrl, CALENDAR_DATETIME_FORMAT)
        } catch (e: Exception) {
            null
        }
    }
}