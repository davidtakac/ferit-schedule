package os.dtakac.feritraspored.common.constants

const val DIALOG_WHATS_NEW = "whats_new"
const val DIALOG_TIME_PICKER = "time_picker"
const val DIALOG_FILTERS_HELP = "filters_help"
const val DIALOG_COURSE_IDENTIFIER_HELP = "course_identifier_help"

/**
 * Default click debounce interval in ms.
 */
const val DEBOUNCE_INTERVAL = 300L

/**
 * Shared preference keys.
 */
object Keys {
    /* Changing the values of these strings will cause the users settings to be lost. */
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