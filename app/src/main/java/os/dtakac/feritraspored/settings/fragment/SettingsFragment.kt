package os.dtakac.feritraspored.settings.fragment

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.inject
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.utils.getStringArray

class SettingsFragment : PreferenceFragmentCompat() {
    private val themes by PreferenceDelegate<ListPreference>(R.string.key_theme)
    private val filters by PreferenceDelegate<EditTextPreference>(R.string.key_filters)
    private val courseIdentifier by PreferenceDelegate<EditTextPreference>(R.string.key_course_identifier)
    private val filtersHelp by PreferenceDelegate<Preference>(R.string.key_filters_help)
    private val timePicker by PreferenceDelegate<Preference>(R.string.key_time_picker)
    private val changelog by PreferenceDelegate<Preference>(R.string.key_changelog)
    private val bugReport by PreferenceDelegate<Preference>(R.string.key_report_bug)
    private val courseIdentifierHelp by PreferenceDelegate<Preference>(R.string.key_course_identifier_help)

    private val prefs: PreferenceRepository by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preference, rootKey)
        initializeViews()
    }

    private fun initializeViews() {
        themes.value = prefs.theme ?: getStringArray(R.array.theme_options)[0]
    }
}