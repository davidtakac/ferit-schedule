package os.dtakac.feritraspored.common.constants

//javascript
const val FUNCTION_START = "(function(){"
const val FUNCTION_END = "}());\n"

//helper values
const val INVALID_HOUR = -1
const val INVALID_MINUTE = -1
const val INVALID_VERSION = -1

//dialog fragment keys
const val DIALOG_WHATS_NEW = "whats_new"
const val DIALOG_TIME_PICKER = "time_picker"
const val DIALOG_FILTERS_HELP = "filters_help"
const val DIALOG_COURSE_IDENTIFIER_HELP = "course_identifier_help"

//todo: remove when refactored time picker
@Deprecated("Don't put listener in args")
const val LISTENER_TIME_PICKER = "listener_time_picker"
@Deprecated("Don't put this in args")
const val DATA_TIME_PICKER = "data_time_picker"