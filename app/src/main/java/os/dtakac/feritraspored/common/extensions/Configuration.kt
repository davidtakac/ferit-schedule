package os.dtakac.feritraspored.common.extensions

import android.content.res.Configuration

fun Configuration.isNightMode(): Boolean {
    return when(uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}