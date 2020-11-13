package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.*
import os.dtakac.feritraspored.common.resources.ResourceRepository

class PreferenceRepositoryImpl(
        private val res: ResourceRepository,
        private val prefs: SharedPreferences
): PreferenceRepository {
    override var isSkipSaturday: Boolean
        get() = prefs.getBoolean(R.string.key_skip_sat, false)
        set(value) = editor { putBoolean(R.string.key_skip_sat, value) }

    override var isSkipDay: Boolean
        get() = prefs.getBoolean(R.string.key_skip_day, false)
        set(value) = editor { putBoolean(R.string.key_skip_day, value) }

    override var filters: String?
        get() = prefs.getString(R.string.key_filters, null)
        set(value) = editor { putString(R.string.key_filters, value) }

    override var programme: String?
        get() = prefs.getString(R.string.key_programme, null)
        set(value) = editor { putString(R.string.key_programme, value) }

    override var year: String?
        get() = prefs.getString(R.string.key_year, null)
        set(value) = editor { putString(R.string.key_year, value) }

    override var timeHour: Int
        get() = prefs.getInt(R.string.key_time_hour, 20)
        set(value) = editor { putInt(R.string.key_time_hour, value) }

    override var timeMinute: Int
        get() = prefs.getInt(R.string.key_time_minute, 0)
        set(value) = editor { putInt(R.string.key_time_minute, value) }

    override var isSettingsModified: Boolean
        get() {
            val wereSettingsModified = prefs.getBoolean(R.string.key_settings_modified, false)
            if(wereSettingsModified) {
                isSettingsModified = false
            }
            return wereSettingsModified
        }
        set(value) = editor { putBoolean(R.string.key_settings_modified, value) }

    override var isLoadOnResume: Boolean
        get() = prefs.getBoolean(R.string.key_load_on_resume, false)
        set(value) = editor { putBoolean(R.string.key_load_on_resume, value) }

    override var previouslyDisplayedWeek: String?
        get() = prefs.getString(R.string.key_prev_week, null)
        set(value) = editor { putString(R.string.key_prev_week, value) }

    override var theme: Int
        get() {
            val defaultTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            return prefs.getString(R.string.key_theme, null)?.toInt() ?: defaultTheme.also { theme = it }
        }
        set(value) = editor { putString(R.string.key_theme, value.toString()) }

    override var version: Int
        get() = prefs.getInt(R.string.key_version, INVALID_VERSION)
        set(value) = editor { putInt(R.string.key_version, value) }

    override var courseIdentifier: String?
        get() = prefs.getString(R.string.key_course_identifier, null)
        set(value) = editor { putString(R.string.key_course_identifier, value) }

    override var isFiltersEnabled: Boolean
        get() = prefs.getBoolean(R.string.key_filters_toggle, false)
        set(value) = editor { putBoolean(R.string.key_filters_toggle, value) }

    private fun getKey(@StringRes keyResId: Int): String {
        return res.getString(keyResId)
    }

    override fun delete(@StringRes keyResId: Int) {
        editor { remove(getKey(keyResId)) }
    }

    override fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun editor(editor: SharedPreferences.Editor.() -> Unit) {
        prefs.edit().also(editor).apply()
    }

    private fun SharedPreferences.getBoolean(@StringRes keyResId: Int, defaultValue: Boolean): Boolean {
        return getBoolean(getKey(keyResId), defaultValue)
    }

    private fun SharedPreferences.Editor.putBoolean(@StringRes keyResId: Int, value: Boolean) {
        putBoolean(getKey(keyResId), value)
    }

    private fun SharedPreferences.getString(@StringRes keyResId: Int, defaultValue: String?): String? {
        return getString(getKey(keyResId), defaultValue)
    }

    private fun SharedPreferences.Editor.putString(@StringRes keyResId: Int, value: String?) {
        putString(getKey(keyResId), value)
    }

    private fun SharedPreferences.getInt(@StringRes keyResId: Int, defaultValue: Int): Int {
        return getInt(getKey(keyResId), defaultValue)
    }

    private fun SharedPreferences.Editor.putInt(@StringRes keyResId: Int, value: Int) {
        putInt(getKey(keyResId), value)
    }
}