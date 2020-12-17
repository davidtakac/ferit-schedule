package os.dtakac.feritraspored.common.data

import android.content.res.Resources
import androidx.annotation.StringRes

data class StringResourceWithArgs(
        @StringRes val content: Int,
        val args: List<String>? = null
) {
    fun buildString(resources: Resources): String {
        val string = resources.getString(content)
        if (!args.isNullOrEmpty()) {
            string.format(*args.toTypedArray())
        }
        return string
    }
}