package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
import androidx.annotation.StringRes

interface PreferenceRepository {
    val isSkipSaturday: Boolean
    val isSkipDay: Boolean
    val filters: String?
    val programme: String?
    val year: String?
    var timeHour: Int
    var timeMinute: Int
    var isReloadToApplySettings: Boolean
    val isLoadOnResume: Boolean
    val theme: Int
    val version: Int
    var courseIdentifier: String?
    val areFiltersEnabled: Boolean
    val isShowTimeOnBlocks: Boolean
    val scheduleTemplate: String

    fun delete(@StringRes keyResId: Int)
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}