package os.dtakac.feritraspored.common.scripts

interface ScriptProvider {
    fun highlightBlocksFunction(filters: Array<String>): String
    fun scrollIntoViewFunction(elementName: String): String
    fun getWeekNumberFunction(): String
    fun hideJunkFunction(): String
    fun darkThemeFunction(): String
    fun timeOnBlocksFunction(): String
}