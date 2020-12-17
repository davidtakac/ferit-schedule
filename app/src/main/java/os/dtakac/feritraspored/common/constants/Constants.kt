package os.dtakac.feritraspored.common.constants

import androidx.appcompat.app.AppCompatDelegate
import os.dtakac.feritraspored.R

const val DEBOUNCE_INTERVAL_MS = 300L
const val SHOW_CHANGELOG = false

object SharedPreferenceKeys {
    const val SKIP_SAT = "skip_saturday_key"
    const val SKIP_DAY = "skip_day_key"
    const val FILTERS = "group_highlight_key"
    const val PROGRAMME = "programme_key"
    const val YEAR = "year_key"
    const val TIME_PICKER = "timepicker_key"
    const val TIME_HOUR = "hour_key"
    const val TIME_MINUTE = "minute_key"
    const val SETTINGS_MODIFIED = "settings_modified"
    const val LOAD_ON_RESUME = "load_on_resume_key"
    const val FILTERS_TOGGLE = "group_toggle_key"
    const val FILTERS_HELP = "group_help_key"
    const val THEME = "prefkey_theme"
    const val CHANGELOG = "changelog_key"
    const val DEV_MESSAGE = "dev_msg_key"
    const val VERSION = "version_key"
    const val IDENTIFIER = "course_identifier"
    const val TIME_ON_BLOCKS = "key_time_on_blocks"
    const val IDENTIFIER_HELP = "course_identifier_help"
    const val SCHEDULE_LANG = "url_key"
}

object DialogKeys {
    const val WHATS_NEW = "whats_new"
    const val TIME_PICKER = "time_picker"
    const val FILTERS_HELP = "filters_help"
    const val COURSE_IDENTIFIER_HELP = "course_identifier_help"
}

val SCHEDULE_LANGUAGES = arrayOf(
        "https://www.ferit.unios.hr/studenti/raspored-nastave-i-ispita/%s/%s",
        "https://www.ferit.unios.hr/students/schedule-of-classes-and-exams/%s/%s"
)

val SUPPORT_EMAILS = arrayOf(
        "developer.takac@gmail.com"
)

val THEME_NAMES_TO_VALUES = linkedMapOf(
        R.string.theme_option_system to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        R.string.theme_option_light to AppCompatDelegate.MODE_NIGHT_NO,
        R.string.theme_option_dark to AppCompatDelegate.MODE_NIGHT_YES
)