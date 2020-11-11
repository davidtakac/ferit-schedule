package os.dtakac.feritraspored.settings.fragment

import androidx.annotation.StringRes
import androidx.preference.Preference
import java.lang.IllegalStateException
import kotlin.reflect.KProperty

class PreferenceDelegate<T: Preference>(@StringRes private val keyResId: Int) {
    private var preference: T? = null

    operator fun getValue(thisRef: SettingsFragment, property: KProperty<*>): T {
        if (preference == null) {
            preference = findPreference(thisRef)
        }
        return preference!!
    }

    private fun findPreference(settingsFragment: SettingsFragment): T {
        val key = settingsFragment.getString(keyResId)
        return settingsFragment.findPreference(key)
                ?: throw IllegalStateException("Could not find preference for key $key.")
    }
}