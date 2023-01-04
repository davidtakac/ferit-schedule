package os.dtakac.feritraspored.common.constants

import androidx.appcompat.app.AppCompatDelegate
import os.dtakac.feritraspored.R

const val DEBOUNCE_INTERVAL_MS = 300L
const val SHOW_CHANGELOG = false

object DialogKeys {
    const val WHATS_NEW = "whats_new"
    const val TIME_PICKER = "time_picker"
    const val FILTERS_HELP = "filters_help"
    const val COURSE_IDENTIFIER_HELP = "course_identifier_help"
}

val SUPPORT_EMAILS = arrayOf(
        "developer.takac@gmail.com"
)

val THEME_NAMES_TO_VALUES = linkedMapOf(
        R.string.theme_option_system to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        R.string.theme_option_light to AppCompatDelegate.MODE_NIGHT_NO,
        R.string.theme_option_dark to AppCompatDelegate.MODE_NIGHT_YES
)