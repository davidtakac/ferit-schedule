package os.dtakac.feritraspored.common.data

import androidx.annotation.StringRes

data class EmailEditorData(
        @StringRes val subject: Int,
        val content: String = ""
)