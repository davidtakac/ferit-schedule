package os.dtakac.feritraspored.common.utils

import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.lang.IllegalStateException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PreferenceManagerDelegate<T: Preference>(
        @StringRes private val keyResId: Int
): ReadOnlyProperty<PreferenceFragmentCompat, T> {
    private var preference: T? = null

    override operator fun getValue(thisRef: PreferenceFragmentCompat, property: KProperty<*>): T {
        if (preference == null) {
            preference = findPreference(thisRef)
        }
        return preference!!
    }

    private fun findPreference(preferenceFragment: PreferenceFragmentCompat): T {
        val key = preferenceFragment.getString(keyResId)
        return preferenceFragment.findPreference(key)
                ?: throw IllegalStateException("Could not find preference for key $key.")
    }
}

fun <T: Preference> preference(@StringRes keyResId: Int) = PreferenceManagerDelegate<T>(keyResId)