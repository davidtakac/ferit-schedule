package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
import androidx.annotation.StringRes
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.*
import os.dtakac.feritraspored.common.resources.ResourceRepository

class PreferenceRepositoryImpl(
        private val resourceRepo: ResourceRepository,
        private val sharedPrefs: SharedPreferences
): PreferenceRepository {
    override var isSkipSaturday: Boolean
        get() = sharedPrefs.getBoolean(R.string.key_skip_sat, false)
        set(value) = editor { putBoolean(R.string.key_skip_sat, value) }

    override var isSkipDay: Boolean
        get() = sharedPrefs.getBoolean(R.string.key_skip_day, false)
        set(value) = editor { putBoolean(R.string.key_skip_day, value) }

    override var filters: String?
        get() = sharedPrefs.getString(R.string.key_groups, null)
        set(value) = editor { putString(R.string.key_groups, value) }

    override var programme: String?
        get() = sharedPrefs.getString(R.string.key_programme, null)
        set(value) = editor { putString(R.string.key_programme, value) }

    override var year: String?
        get() = sharedPrefs.getString(R.string.key_year, null)
        set(value) = editor { putString(R.string.key_year, value) }

    override var timeHour: Int
        get() = sharedPrefs.getInt(R.string.key_time_hour, DEFAULT_HOUR)
        set(value) = editor { putInt(R.string.key_time_hour, value) }

    override var timeMinute: Int
        get() = sharedPrefs.getInt(R.string.key_time_minute, DEFAULT_MINUTE)
        set(value) = editor { putInt(R.string.key_time_minute, value) }

    override var isSettingsModified: Boolean
        get() = sharedPrefs.getBoolean(R.string.key_settings_modified, false)
        set(value) = editor { putBoolean(R.string.key_settings_modified, value) }

    override var isLoadOnResume: Boolean
        get() = sharedPrefs.getBoolean(R.string.key_load_on_resume, false)
        set(value) = editor { putBoolean(R.string.key_load_on_resume, value) }

    override var previouslyDisplayedWeek: String?
        get() = sharedPrefs.getString(R.string.key_prev_week, null)
        set(value) = editor { putString(R.string.key_prev_week, value) }

    override var theme: String?
        get() = sharedPrefs.getString(R.string.key_theme, null)
        set(value) = editor { putString(R.string.key_theme, value) }

    override var version: Int
        get() = sharedPrefs.getInt(R.string.key_version, INVALID_VERSION)
        set(value) = editor { putInt(R.string.key_version, value) }

    override var courseIdentifier: String?
        get() = sharedPrefs.getString(R.string.key_course_identifier, null)
        set(value) = editor { putString(R.string.key_course_identifier, value) }

    override var isFiltersEnabled: Boolean
        get() = sharedPrefs.getBoolean(R.string.key_groups_toggle, false)
        set(value) = editor { putBoolean(R.string.key_groups_toggle, value) }

    override fun getKey(@StringRes keyResId: Int): String {
        return resourceRepo.getString(keyResId)
    }

    override fun delete(@StringRes keyResId: Int) {
        editor { remove(getKey(keyResId)) }
    }

    private fun editor(editor: SharedPreferences.Editor.() -> Unit) {
        sharedPrefs.edit().also(editor).apply()
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