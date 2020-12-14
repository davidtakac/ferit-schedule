package os.dtakac.feritraspored.schedule.data

data class JavascriptData(
        val js: String,
        val callback: (value: String) -> Unit = {}
)