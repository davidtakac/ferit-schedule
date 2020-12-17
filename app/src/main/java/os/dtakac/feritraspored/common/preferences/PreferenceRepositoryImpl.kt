package os.dtakac.feritraspored.common.preferences

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.common.constants.SCHEDULE_LANGUAGES
import os.dtakac.feritraspored.common.constants.SharedPreferenceKeys
import os.dtakac.feritraspored.common.extensions.timeFormat
import os.dtakac.feritraspored.common.extensions.toLocalTime
import java.time.LocalTime

class PreferenceRepositoryImpl(
        private val prefs: SharedPreferences
): PreferenceRepository {
    override val isSkipSaturday: Boolean
        get() = prefs.getBoolean(SharedPreferenceKeys.SKIP_SAT, false)

    override val isSkipDay: Boolean
        get() = prefs.getBoolean(SharedPreferenceKeys.SKIP_DAY, false)

    override val filters: String?
        get() = prefs.getString(SharedPreferenceKeys.FILTERS, null)

    override val programme: String?
        get() = prefs.getString(SharedPreferenceKeys.PROGRAMME, null)

    override val year: String?
        get() = prefs.getString(SharedPreferenceKeys.YEAR, null)

    override var time: LocalTime
        get() {
            val value = prefs.getString(SharedPreferenceKeys.TIME_PICKER, null)
            return if (value == null) {
                // migrate from old preferences if possible
                val hourValue = prefs.getInt(SharedPreferenceKeys.TIME_HOUR, 20)
                val minuteValue = prefs.getInt(SharedPreferenceKeys.TIME_MINUTE, 0)
                // create default time and persist it
                val defaultTime = LocalTime.of(hourValue, minuteValue)
                editor { putString(SharedPreferenceKeys.TIME_PICKER, defaultTime.timeFormat()) }
                // delete old preferences
                delete(SharedPreferenceKeys.TIME_HOUR)
                delete(SharedPreferenceKeys.TIME_MINUTE)

                defaultTime
            } else {
                value.toLocalTime()
            }
        }
        set(value) {
            editor { putString(SharedPreferenceKeys.TIME_PICKER, value.timeFormat()) }
        }

    override var isReloadToApplySettings: Boolean
        get() {
            val wereSettingsModified = prefs.getBoolean(SharedPreferenceKeys.SETTINGS_MODIFIED, false)
            if(wereSettingsModified) {
                isReloadToApplySettings = false
            }
            return wereSettingsModified
        }
        set(value) = editor { putBoolean(SharedPreferenceKeys.SETTINGS_MODIFIED, value) }

    override val isLoadOnResume: Boolean
        get() = prefs.getBoolean(SharedPreferenceKeys.LOAD_ON_RESUME, false)

    override val theme: Int
        get() {
            val value = prefs.getString(SharedPreferenceKeys.THEME, null)?.toIntOrNull()
            return if(value == null) {
                val defaultTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                editor { putString(SharedPreferenceKeys.THEME, defaultTheme.toString()) }
                defaultTheme
            } else {
                value
            }
        }

    override val version: Int
        get() {
            val versionValue = prefs.getInt(SharedPreferenceKeys.VERSION, -1)
            if(versionValue < BuildConfig.VERSION_CODE) {
                editor { putInt(SharedPreferenceKeys.VERSION, BuildConfig.VERSION_CODE) }
            }
            return versionValue
        }

    override val courseIdentifier: String
        get() {
            val value =  prefs.getString(SharedPreferenceKeys.IDENTIFIER, null)
            return if (value == null) {
                // migrate from old preferences
                val yearValue = prefs.getString(SharedPreferenceKeys.YEAR, "1")
                val programmeValue = prefs.getString(SharedPreferenceKeys.PROGRAMME, "1")
                // create new course identifier
                val defaultIdentifier = "$yearValue-$programmeValue"
                editor { putString(SharedPreferenceKeys.IDENTIFIER, defaultIdentifier) }
                // delete old preferences
                delete(SharedPreferenceKeys.YEAR)
                delete(SharedPreferenceKeys.PROGRAMME)

                defaultIdentifier
            } else {
                value
            }
        }

    override val areFiltersEnabled: Boolean
        get() = prefs.getBoolean(SharedPreferenceKeys.FILTERS_TOGGLE, false)

    override val isShowTimeOnBlocks: Boolean
        get() = prefs.getBoolean(SharedPreferenceKeys.TIME_ON_BLOCKS, false)

    override val scheduleTemplate: String
        get() {
            val template = prefs.getString(SharedPreferenceKeys.SCHEDULE_LANG, null)
            return if(template == null) {
                val defaultUrl = SCHEDULE_LANGUAGES[0]
                editor { putString(SharedPreferenceKeys.SCHEDULE_LANG, defaultUrl) }
                defaultUrl
            } else {
                template
            }
        }

    private fun editor(operation: SharedPreferences.Editor.() -> Unit) {
        prefs.edit().also(operation).apply()
    }

    private fun delete(key: String) {
        editor { remove(key) }
    }
}