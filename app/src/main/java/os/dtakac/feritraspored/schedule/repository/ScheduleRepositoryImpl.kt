package os.dtakac.feritraspored.schedule.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import os.dtakac.feritraspored.schedule.data.ScheduleData

class ScheduleRepositoryImpl : ScheduleRepository {
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
        val title = withContext(Dispatchers.Default) {
            document.getTitle()
        }
        // clean document and apply transformations
        withContext(Dispatchers.Default) {
            document.removeJunk()
            if (showTimeOnBlocks) document.applyTimeOnBlocks()
            if (filters.isNotEmpty()) document.applyFilters(filters)
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
        // removes userway
        selectFirst("head").select("script")[1].remove()
        // removes body junk
        select("#vrh").remove()
        select("#izbornik").remove()
        select("#mizbornik").remove()
        select("#mpodizbornik").remove()
        select("#header-kategorija").remove()
        select("#img-out").remove()
        select("#podnozje").remove()
        select("#gototopdiv").remove()
        select("#tekst-dokumenti").remove()
        selectFirst(".tekst-dokumenti").remove()
        // removes schedule header
        select("#tekst").remove()
        select("#raspored > .odabir").remove()
        // removes scrolling to day behavior
        select(".naziv-dan > a").removeAttr("href")
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
            if (time != null) {
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
        return if (title == null
                || title.isBlank()
                || title.isEmpty()
                || title == "null"
                || title == "undefined") {
                    null
        } else {
            title
        }
    }
}