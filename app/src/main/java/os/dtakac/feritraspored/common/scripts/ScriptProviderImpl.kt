package os.dtakac.feritraspored.common.scripts

import android.content.res.AssetManager
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.resources.ResourceRepository
import java.lang.StringBuilder
import java.util.*

class ScriptProviderImpl(
        private val assetManager: AssetManager,
        private val resourceRepository: ResourceRepository
): ScriptProvider {
    override fun highlightBlocksFunction(filters: List<String>): String {
        val stringBuilder = StringBuilder()
        filters.forEach {
            //varies for dark and light mode
            val scriptPath = resourceRepository.getString(R.string.highlight_blocks_path)
            val script = getScript(scriptPath).format(it)
            stringBuilder.append(script)
        }
        return "(function(){$stringBuilder}());"
    }

    override fun scrollIntoViewFunction(elementName: String): String {
        return getScript("scroll-into-view.js").format(elementName)
    }

    override fun getWeekNumberFunction(): String {
        return getScript("get-week-number.js")
    }

    override fun hideJunkFunction(): String {
        return getScript("hide-junk.js")
    }

    override fun darkThemeFunction(): String {
        return getScript("dark-theme.js")
    }

    override fun timeOnBlocksFunction(): String {
        return getScript("time-on-blocks.js")
    }

    private fun getScript(fileName: String): String {
        val scanner = Scanner(assetManager.open(fileName))
        val stringBuilder = StringBuilder()

        while(scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine())
        }

        return stringBuilder.toString()
    }
}