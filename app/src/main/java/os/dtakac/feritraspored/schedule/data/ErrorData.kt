package os.dtakac.feritraspored.schedule.data

import androidx.annotation.StringRes

data class ErrorData(
        @StringRes val message: Int,
        val exception: Exception? = null
)