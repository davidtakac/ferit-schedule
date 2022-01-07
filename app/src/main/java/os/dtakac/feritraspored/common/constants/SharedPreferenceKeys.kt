package os.dtakac.feritraspored.common.constants

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
    @Deprecated(
            message = "Deprecated because schedule URLs change often and it is likely that users will have stale templates in their shared preferences after an update.",
            level = DeprecationLevel.ERROR,
            replaceWith = ReplaceWith("SCHEDULE_LANG", "os.dtakac.feritraspored.common.constants.SharedPreferenceKeys")
    )
    const val SCHEDULE_URL = "url_key"
    const val SCHEDULE_LANG = "schedule_lang"
}