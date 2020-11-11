package os.dtakac.feritraspored.common.scripts

import android.content.res.AssetManager
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.FUNCTION_END
import os.dtakac.feritraspored.common.constants.FUNCTION_START
import os.dtakac.feritraspored.common.resources.ResourceRepository
import java.lang.StringBuilder
import java.util.*

class ScriptProviderImpl(
        private val assetManager: AssetManager,
        private val resourceRepository: ResourceRepository
): ScriptProvider {
    override fun highlightBlocksFunction(filters: Array<String>): String {
        val stringBuilder = StringBuilder()
        filters.forEach {
            //varies for dark and light mode
            val scriptPath = resourceRepository.getString(R.string.highlight_blocks_path)
            val script = getScript(scriptPath).format(it)
            stringBuilder.append(script)
        }
        return "$FUNCTION_START${stringBuilder}$FUNCTION_END"
    }

    override fun scrollIntoViewFunction(elementName: String): String {
        return "$FUNCTION_START${getScript("scroll-into-view.js").format(elementName)}$FUNCTION_END"
    }

    override fun getWeekNumberFunction(): String {
        return "$FUNCTION_START${getScript("get-week-number.js")}$FUNCTION_END"
    }

    override fun hideJunkFunction(): String {
        return "$FUNCTION_START${getScript("hide-junk.js")}$FUNCTION_END"
    }

    override fun darkThemeFunction(): String {
        return "$FUNCTION_START${getScript("dark-theme.js")}$FUNCTION_END"
    }

    override fun timeOnBlocksFunction(): String {
        return "$FUNCTION_START${getScript("time-on-blocks.js")}$FUNCTION_END"
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