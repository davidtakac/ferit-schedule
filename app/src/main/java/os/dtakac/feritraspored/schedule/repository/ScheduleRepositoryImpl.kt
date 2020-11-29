package os.dtakac.feritraspored.schedule.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.BORDER_STYLE
import os.dtakac.feritraspored.common.constants.DARK_BACKGROUND_IMAGE
import os.dtakac.feritraspored.common.extensions.setBackgroundColor
import os.dtakac.feritraspored.common.extensions.setColor
import os.dtakac.feritraspored.common.extensions.setStyle
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.extensions.urlFormat
import os.dtakac.feritraspored.schedule.data.ScheduleData
import java.time.LocalDate

class ScheduleRepositoryImpl(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository
): ScheduleRepository {
    override suspend fun getScheduleData(withDate: LocalDate): ScheduleData {
        val baseUrl = res.getString(R.string.template_schedule)
                .format(withDate.urlFormat(), prefs.courseIdentifier)

        val document = withContext(Dispatchers.IO) {
            Jsoup.connect(baseUrl).get()
        }

        withContext(Dispatchers.IO) {
            document.hideJunk()
            document.applyDarkTheme()
        }

        return ScheduleData(
                baseUrl = baseUrl,
                html = document.toString(),
                encoding = "UTF-8",
                mimeType = "text/html"
        )
    }

    private fun Document.hideJunk() {
        selectFirst("#pagewrap").children().not(".narrow-down").remove()
        selectFirst(".narrow-down").children().not("#content-contain").remove()
        selectFirst("#content").children().not("#raspored").remove()
        selectFirst("#raspored").children().not(".vrijeme, .vrijeme-mobitel, .dan, .odabir").remove()
        selectFirst("#raspored > .odabir").remove()
        selectFirst("#izbor-studija").remove()
        select(".naziv-dan > a").removeAttr("href")
        select("script[src=https://cdn.userway.org/widget.js]").remove()
    }

    private fun Document.applyDarkTheme() {
        val background = res.getColorHex(R.color.almostBlack)
        selectFirst("#raspored").setBackgroundColor(background)
        selectFirst("#content-contain").setBackgroundColor(background)
        select(".vrijeme-mobitel > tok").setStyle("border-right: $BORDER_STYLE")
        //messes up page layout
        //select(".raspored > div.dan > div.tok").setStyle("background-image: $DARK_BACKGROUND_IMAGE")
        select(".vrijeme-mobitel > .tok > .satnica").setStyle("border-top: $BORDER_STYLE; border-bottom: $BORDER_STYLE")

        val backgroundElevated = res.getColorHex(R.color.gray900)
        val textColor = res.getColorHex(R.color.darkElementTextColor)
        select(".satnica").setBackgroundColor(backgroundElevated)
        select(".naziv-dan").setBackgroundColor(backgroundElevated)
        select(".naziv-dan > a").setStyle("background-color: $backgroundElevated; color: $textColor")
        select(".vrijeme-mobitel > .satnica").setColor(textColor)

        //makes blocks stack on top of each other and lose height
        /*val darkTextColor = res.getColorHex(R.color.darkBlocksTextColor)
        select(".blokovi").setStyle("border-top: none")
        select(".blokovi").not(".Ne").select("p").setColor(darkTextColor)
        select(".PR").setBackgroundColor(res.getColorHex(R.color.darkPrBlock))
        select(".AV").setBackgroundColor(res.getColorHex(R.color.darkAvBlock))
        select(".LV").setBackgroundColor(res.getColorHex(R.color.darkLvBlock))
        select(".IS").setBackgroundColor(res.getColorHex(R.color.darkIsBlock))
        select(".KV").setBackgroundColor(res.getColorHex(R.color.darkKvBlock))*/
    }
}