package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.resources.ResourceRepository

class PreferenceRepositoryImpl(
        private val res: ResourceRepository,
        private val prefs: SharedPreferences
): PreferenceRepository {
    init {
        migrateToCourseIdentifierPreference()
    }

    override val isSkipSaturday: Boolean
        get() = prefs.getBoolean(R.string.key_skip_sat, false)

    override val isSkipDay: Boolean
        get() = prefs.getBoolean(R.string.key_skip_day, false)

    override val filters: String?
        get() = prefs.getString(R.string.key_filters, null)

    override val programme: String?
        get() = prefs.getString(R.string.key_programme, null)

    override val year: String?
        get() = prefs.getString(R.string.key_year, null)

    override var timeHour: Int
        get() = prefs.getInt(R.string.key_time_hour, 20)
        set(value) = editor { putInt(R.string.key_time_hour, value) }

    override var timeMinute: Int
        get() = prefs.getInt(R.string.key_time_minute, 0)
        set(value) = editor { putInt(R.string.key_time_minute, value) }

    override var isReloadToApplySettings: Boolean
        get() {
            val wereSettingsModified = prefs.getBoolean(R.string.key_settings_modified, false)
            if(wereSettingsModified) {
                isReloadToApplySettings = false
            }
            return wereSettingsModified
        }
        set(value) = editor { putBoolean(R.string.key_settings_modified, value) }

    override val isLoadOnResume: Boolean
        get() = prefs.getBoolean(R.string.key_load_on_resume, false)

    override val theme: Int
        get() {
            val value = prefs.getString(R.string.key_theme, null)?.toIntOrNull()
            return if(value == null) {
                val defaultTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                editor { putString(R.string.key_theme, defaultTheme.toString()) }
                defaultTheme
            } else {
                value
            }
        }

    override val version: Int
        get() {
            val versionValue = prefs.getInt(R.string.key_version, -1)
            if(versionValue < BuildConfig.VERSION_CODE) {
                editor { putInt(R.string.key_version, BuildConfig.VERSION_CODE) }
            }
            return versionValue
        }

    override var courseIdentifier: String?
        get() = prefs.getString(R.string.key_course_identifier, null)
        set(value) = editor { putString(R.string.key_course_identifier, value) }

    override val areFiltersEnabled: Boolean
        get() = prefs.getBoolean(R.string.key_filters_toggle, false)

    override val isShowTimeOnBlocks: Boolean
        get() = prefs.getBoolean(R.string.key_time_on_blocks, false)

    override val scheduleTemplate: String
        get() {
            val template = prefs.getString(R.string.key_schedule_language, null)
            return if(template == null) {
                val defaultUrl = res.getStringArray(R.array.schedule_languages)[0]
                editor { putString(R.string.key_schedule_language, defaultUrl) }
                defaultUrl
            } else {
                template
            }
        }

    override fun delete(@StringRes keyResId: Int) {
        editor { remove(res.getString(keyResId)) }
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
        return getBoolean(res.getString(keyResId), defaultValue)
    }

    private fun SharedPreferences.Editor.putBoolean(@StringRes keyResId: Int, value: Boolean) {
        putBoolean(res.getString(keyResId), value)
    }

    private fun SharedPreferences.getString(@StringRes keyResId: Int, defaultValue: String?): String? {
        return getString(res.getString(keyResId), defaultValue)
    }

    private fun SharedPreferences.Editor.putString(@StringRes keyResId: Int, value: String?) {
        putString(res.getString(keyResId), value)
    }

    private fun SharedPreferences.getInt(@StringRes keyResId: Int, defaultValue: Int): Int {
        return getInt(res.getString(keyResId), defaultValue)
    }

    private fun SharedPreferences.Editor.putInt(@StringRes keyResId: Int, value: Int) {
        putInt(res.getString(keyResId), value)
    }

    private fun migrateToCourseIdentifierPreference() {
        if(courseIdentifier == null) {
            if(year == null || programme == null) return
            courseIdentifier = "${year}-${programme}"
            delete(R.string.key_year)
            delete(R.string.key_programme)
        }
    }
}