package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
import androidx.annotation.StringRes

interface PreferenceRepository {
    var isSkipSaturday: Boolean
    var isSkipDay: Boolean
    var filters: String?
    var programme: String?
    var year: String?
    var timeHour: Int
    var timeMinute: Int
    var isSettingsModified: Boolean
    var isLoadOnResume: Boolean
    var previouslyDisplayedWeek: String?
    var theme: String?
    var version: Int
    var courseIdentifier: String?
    var isFiltersEnabled: Boolean

    fun getKey(@StringRes keyResId: Int): String
    fun delete(@StringRes keyResId: Int)
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}