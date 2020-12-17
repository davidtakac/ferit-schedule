package os.dtakac.feritraspored.common.data

import androidx.annotation.StringRes

data class EmailEditorData(
        @StringRes val subject: Int? = null,
        val content: String = ""
)