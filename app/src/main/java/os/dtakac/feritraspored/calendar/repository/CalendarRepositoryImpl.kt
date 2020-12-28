package os.dtakac.feritraspored.calendar.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import os.dtakac.feritraspored.calendar.response.CalendarResponse
import os.dtakac.feritraspored.calendar.response.EventResponse
import os.dtakac.feritraspored.common.constants.CALENDAR_URL_PATTERN
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CalendarRepositoryImpl(
        private val contentResolver: ContentResolver
) : CalendarRepository {
    private val zoneId = ZoneId.of("GMT+1")

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
        val document = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Jsoup.connect(scheduleUrl).get()
        }
        return extractEventsFromBlocks(document)
    }

    override suspend fun addEvents(calendarId: String, events: List<EventResponse>) {
        val bulkValues = withContext(Dispatchers.Default) {
            events.map {
                ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, it.start.toInstant().toEpochMilli())
                    put(CalendarContract.Events.DTEND, it.end.toInstant().toEpochMilli())
                    put(CalendarContract.Events.TITLE, it.title)
                    put(CalendarContract.Events.DESCRIPTION, it.description)
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.EVENT_TIMEZONE, zoneId.id)
                }
            }.toTypedArray()
        }
        withContext(Dispatchers.IO) {
            contentResolver.bulkInsert(
                    CalendarContract.Events.CONTENT_URI,
                    bulkValues
            )
        }
    }

    private suspend fun extractEventsFromBlocks(document: Document): List<EventResponse> {
        val blocks = withContext(Dispatchers.Default) {
            document.select(".blokovi")
        }
        val events = mutableListOf<EventResponse>()
        withContext(Dispatchers.Default) {
            for (i in blocks.indices) {
                val blockText = blocks[i].select("p")
                val blockHide = blocks[i].select(".hide")

                val dates = Uri.parse(blockHide
                        .select("a[href*=calendar]")
                        .attr("href")
                ).getQueryParameter("dates")?.split("/")
                val start = parseDate(dates?.getOrNull(0))
                val end = parseDate(dates?.getOrNull(1))
                if (start == null || end == null) {
                    continue
                }

                val name = blockText.textNodes().getOrNull(0)?.text()?.trim()
                val type = blockHide.textNodes().getOrNull(0)?.text()?.trim()
                val staff = blockHide
                        .select("a[href*=imenik-djelatnika], a[href*=staff-directory]")
                        .text()
                        .trim()
                val group = blockHide
                        .select("a[href*=grupa]")
                        .getOrNull(0)
                        ?.text()
                        ?.split("Otvori grupu")
                        ?.getOrNull(1)
                        ?.trim()
                val location = blockText.textNodes().getOrNull(1)?.text()?.trim()

                events.add(EventResponse(
                        id = i.toString(),
                        start = start,
                        end = end,
                        title = name,
                        description = "$group - $staff: $type",
                        location = location
                ))
            }
        }
        return events
    }

    private suspend fun extractEventsFromCalendarLinks(document: Document): List<EventResponse> {
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
                        id = i.toString(),
                        start = start,
                        end = end,
                        title = title,
                        description = description,
                        location = null
                ))
            }
        }
        return events
    }

    private fun parseDate(dateFromUrl: String?): ZonedDateTime? {
        return try {
            LocalDateTime
                    .parse(dateFromUrl, DateTimeFormatter.ofPattern(CALENDAR_URL_PATTERN))
                    .atZone(ZoneOffset.UTC)
                    .withZoneSameInstant(zoneId)
        } catch (e: Exception) {
            null
        }
    }
}