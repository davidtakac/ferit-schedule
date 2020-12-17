package os.dtakac.feritraspored.common.data

import androidx.annotation.StringRes

data class EmailEditorData(
        @StringRes val subject: Int? = null,
        @StringRes val content: Int? = null,
        val contentArgs: List<String>? = null
)