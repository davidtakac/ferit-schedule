package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
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
    var courseIdentifier: String?
    val areFiltersEnabled: Boolean
    val isShowTimeOnBlocks: Boolean
    val scheduleTemplate: String

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}