package os.dtakac.feritraspored.schedule.data

data class JavascriptData(
        val javascript: String,
        val valueListener: (value: String) -> Unit = {}
)