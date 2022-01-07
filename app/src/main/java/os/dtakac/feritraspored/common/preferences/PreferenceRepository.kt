package os.dtakac.feritraspored.common.preferences

import os.dtakac.feritraspored.common.constants.ScheduleLanguage
import java.time.LocalTime

interface PreferenceRepository {
    val isSkipSaturday: Boolean
    val isSkipDay: Boolean
    val filters: String?
    val programme: String?
    val year: String?
    var time: LocalTime
    var isReloadToApplySettings: Boolean
    val isLoadOnResume: Boolean
    val theme: Int
    val version: Int
    val courseIdentifier: String
    val areFiltersEnabled: Boolean
    val isShowTimeOnBlocks: Boolean
    @Deprecated(
            message = "Deprecated because schedule templates change often and it is likely that users will have stale templates in their shared preferences after an update.",
            level = DeprecationLevel.ERROR,
            replaceWith = ReplaceWith("getScheduleUrl()", "os.dtakac.feritraspored.common.constants.getScheduleUrl", "os.dtakac.feritraspored.common.constants.ScheduleLanguage")
    )
    val scheduleTemplate: String
    val scheduleLanguage: ScheduleLanguage
}