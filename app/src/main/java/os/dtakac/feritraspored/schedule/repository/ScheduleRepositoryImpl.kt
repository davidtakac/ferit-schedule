package os.dtakac.feritraspored.schedule.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.extensions.addToStyle
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.extensions.urlFormat
import os.dtakac.feritraspored.schedule.data.ScheduleData
import java.time.LocalDate

class ScheduleRepositoryImpl(
        private val res: ResourceRepository
): ScheduleRepository {
    override suspend fun getScheduleData(
            withDate: LocalDate,
            courseIdentifier: String,
            showTimeOnBlocks: Boolean,
            filters: List<String>,
            applyDarkTheme: Boolean
    ): ScheduleData {
        val scheduleUrl = getScheduleUrl(withDate, courseIdentifier)
        val document = getDocument(scheduleUrl)
        val title = document.getTitle()
        document.applyTransformations(showTimeOnBlocks, applyDarkTheme, filters)
        return ScheduleData(
                baseUrl = scheduleUrl,
                html = document.toString(),
                encoding = "UTF-8",
                mimeType = "text/html",
                title = title ?: res.getString(R.string.label_schedule)
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getDocument(url: String): Document = withContext(Dispatchers.IO) {
        Jsoup.connect(url).get()
    }

    private fun getScheduleUrl(withDate: LocalDate, courseIdentifier: String) = res
            .getString(R.string.template_schedule)
            .format(withDate.urlFormat(), courseIdentifier)

    private suspend fun Document.applyTransformations(
            showTimeOnBlocks: Boolean,
            applyDarkTheme: Boolean,
            filters: List<String>
    ) = withContext(Dispatchers.IO) {
        hideJunk()
        if(showTimeOnBlocks) showTimeOnBlocks()
        if(applyDarkTheme) applyDarkTheme()
        if(filters.isNotEmpty()) highlightBlocks(filters)
    }

    private fun Document.hideJunk() {
        selectFirst("#pagewrap").children().not(".narrow-down").remove()
        selectFirst(".narrow-down").children().not("#content-contain").remove()
        selectFirst("#content").children().not("#raspored").remove()
        selectFirst("#raspored").children()
                .not(".vrijeme, .vrijeme-mobitel, .dan, .odabir").remove()
        selectFirst("#raspored .odabir").remove()
        selectFirst("#izbor-studija").remove()
        select(".naziv-dan a").removeAttr("href")
        select("script[src*=cdn.userway.org]").remove()
        select("script[src*=googletagmanager]").remove()
        select("script:containsData(var blinker;)").remove()
        select("script:containsData(function gtag)").remove()
        select("script:containsData(hs.graphicsDir)").remove()
        select("script[src*=FileSaver]").remove()
        select("script[src*=highslide]").remove()
        select("script[src*=responsiveslides]").remove()
    }

    private fun Document.applyDarkTheme() {
        head().append("<style>${res.readFromAssets("dark_theme.css")}</style>")
    }

    private fun Document.showTimeOnBlocks() {
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

    private fun Document.highlightBlocks(filters: List<String>) {
        filters.forEach { filter ->
            val blocks = select("div.blokovi:contains($filter)")
            blocks.forEach {
                it.addToStyle("border-style: solid; " +
                        "border-color: ${res.getColorHex(R.color.colorHighlightBlock)}; " +
                        "border-width: 2px; "
                )
            }
        }
    }

    private suspend fun Document.getTitle(): String? = withContext(Dispatchers.IO) {
        val title = select("h3.odabir p a")
                .getOrNull(1)?.text()
                ?.removeSurrounding("\"")

        when {
            title == null
            || title.isBlank()
            || title.isEmpty()
            || title == "null"
            || title == "undefined" -> null
            else -> title
        }
    }
}