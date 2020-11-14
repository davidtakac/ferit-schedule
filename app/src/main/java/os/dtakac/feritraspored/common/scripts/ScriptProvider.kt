package os.dtakac.feritraspored.common.scripts

interface ScriptProvider {
    fun highlightBlocksFunction(filters: List<String>): String
    fun scrollIntoViewFunction(elementName: String): String
    fun weekNumberFunction(): String
    fun hideJunkFunction(): String
    fun darkThemeFunction(): String
    fun timeOnBlocksFunction(): String
}