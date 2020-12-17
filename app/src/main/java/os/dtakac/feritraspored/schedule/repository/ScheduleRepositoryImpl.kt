package os.dtakac.feritraspored.schedule.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import os.dtakac.feritraspored.schedule.data.ScheduleData

class ScheduleRepositoryImpl: ScheduleRepository {
    override suspend fun getScheduleData(
            scheduleUrl: String,
            showTimeOnBlocks: Boolean,
            filters: List<String>,
            lightThemeCss: String,
            darkThemeCss: String
    ): ScheduleData {
        // fetch document
        val document = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Jsoup.connect(scheduleUrl).get()
        }
        // get page title before document is cleaned
        val title = withContext(Dispatchers.IO) {
            document.getTitle()
        }
        // clean document and apply transformations
        withContext(Dispatchers.IO) {
            document.removeJunk()
            if(showTimeOnBlocks) document.applyTimeOnBlocks()
            if(filters.isNotEmpty()) document.applyFilters(filters)
        }

        return ScheduleData(
                baseUrl = scheduleUrl,
                data = document.applyCss(lightThemeCss).toString(),
                dataDark = document.applyCss(darkThemeCss).toString(),
                encoding = "UTF-8",
                mimeType = "text/html",
                title = title
        )
    }

    private fun Document.removeJunk() = apply {
        selectFirst("#pagewrap").children().not(".narrow-down").remove()
        selectFirst(".narrow-down").children().not("#content-contain").remove()
        selectFirst("#content").children().not("#raspored").remove()
        selectFirst("#raspored").children()
                .not(".vrijeme, .vrijeme-mobitel, .dan, .odabir").remove()
        selectFirst("#raspored .odabir").remove()
        selectFirst("#izbor-studija").remove()
        select(".naziv-dan a").removeAttr("href")
        select("script").remove()
    }

    private fun Document.applyCss(css: String) = apply {
        head().append("<style>$css</style>")
    }

    private fun Document.applyTimeOnBlocks() = apply {
        select(".blokovi").forEach {
            val time = it.selectFirst("span.hide")
                    .textNodes()
                    .getOrNull(3)
                    ?.text()
                    ?.trim()
            if(time != null) {
                it.selectFirst(".thumbnail p").append("<br/>$time")
            }
        }
    }

    private fun Document.applyFilters(filters: List<String>) = apply {
        filters.forEach { filter ->
            select("div.blokovi:contains($filter)")
                    .addClass("android_app_selected")
        }
    }

    private fun Document.getTitle(): String? {
        val title = select("h3.odabir p a")
                .getOrNull(1)
                ?.text()
                ?.removeSurrounding("\"")
        return when {
            title == null
            || title.isBlank()
            || title.isEmpty()
            || title == "null"
            || title == "undefined" -> null
            else -> title
        }
    }
}