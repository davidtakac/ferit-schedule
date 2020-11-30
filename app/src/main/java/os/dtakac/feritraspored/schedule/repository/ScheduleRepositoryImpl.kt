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
        val baseUrl = res.getString(R.string.template_schedule)
                .format(withDate.urlFormat(), courseIdentifier)
        val document = withContext(Dispatchers.IO) { Jsoup.connect(baseUrl).get() }
        val title = withContext(Dispatchers.IO) { document.getTitle() }
        //apply transformations to document
        withContext(Dispatchers.IO) {
            document.hideJunk()
            if(showTimeOnBlocks) document.showTimeOnBlocks()
            if(applyDarkTheme) document.applyDarkTheme()
            if(filters.isNotEmpty()) document.highlightBlocks(filters)
        }
        return ScheduleData(
                baseUrl = baseUrl,
                html = document.toString(),
                encoding = "UTF-8",
                mimeType = "text/html",
                title = title ?: res.getString(R.string.label_schedule)
        )
    }

    private fun Document.hideJunk() {
        selectFirst("#pagewrap").children().not(".narrow-down").remove()
        selectFirst(".narrow-down").children().not("#content-contain").remove()
        selectFirst("#content").children().not("#raspored").remove()
        selectFirst("#raspored").children().not(".vrijeme, .vrijeme-mobitel, .dan, .odabir").remove()
        selectFirst("#raspored .odabir").remove()
        selectFirst("#izbor-studija").remove()
        select(".naziv-dan a").removeAttr("href")
        select("script[src=https://cdn.userway.org/widget.js]").remove()
    }

    private fun Document.applyDarkTheme() {
        head().append("<style>${res.readFromAssets("dark_theme.css")}</style>")
    }

    private fun Document.showTimeOnBlocks() {
        select(".blokovi").forEach {
            val time = it.selectFirst("span.hide").textNodes().getOrNull(3)?.text() ?: ""
            it.selectFirst(".thumbnail p").append("<br/>$time")
        }
    }

    private fun Document.highlightBlocks(filters: List<String>) {
        filters.forEach { filter ->
            val blocks = select("div.blokovi:contains($filter)")
            blocks.forEach {
                it.addToStyle("border-style: solid; border-color: ${res.getColorHex(R.color.highlightColor)}; border-width: 2px; ")
            }
        }
    }

    private fun Document.getTitle(): String? {
        val title = select("h3.odabir p a")
                .getOrNull(1)?.text()
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