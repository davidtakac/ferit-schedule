package os.dtakac.feritraspored.settings.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.inject
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.*
import os.dtakac.feritraspored.common.extensions.*
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.view.dialog_time_picker.TimePickerDialogFragment
import os.dtakac.feritraspored.settings.summaryprovider.EditTextPreferenceSummaryProvider
import os.dtakac.feritraspored.settings.summaryprovider.ListPreferenceSummaryProvider

@Suppress("unused")
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val themes: ListPreference by preference(SharedPreferenceKeys.THEME)
    private val filters: EditTextPreference by preference(SharedPreferenceKeys.FILTERS)
    private val courseIdentifier: EditTextPreference by preference(SharedPreferenceKeys.IDENTIFIER)
    private val filtersHelp: Preference by preference(SharedPreferenceKeys.FILTERS_HELP)
    private val timePicker: Preference by preference(SharedPreferenceKeys.TIME_PICKER)
    private val changelog: Preference by preference(SharedPreferenceKeys.CHANGELOG)
    private val messageToDeveloper: Preference by preference(SharedPreferenceKeys.DEV_MESSAGE)
    private val courseIdentifierHelp: Preference by preference(SharedPreferenceKeys.IDENTIFIER_HELP)
    private val scheduleLanguages: ListPreference by preference(SharedPreferenceKeys.SCHEDULE_LANG)

    private val prefsRepo: PreferenceRepository by inject()
    private val sharedPrefs: SharedPreferences by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeClickListeners()
        initializeViews()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key != SharedPreferenceKeys.THEME && key != SharedPreferenceKeys.SETTINGS_MODIFIED) {
            prefsRepo.isReloadToApplySettings = true
        }
        when(key) {
            SharedPreferenceKeys.THEME -> onThemeChanged()
            SharedPreferenceKeys.TIME_PICKER -> onTimeChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    private fun onThemeChanged() {
        AppCompatDelegate.setDefaultNightMode(prefsRepo.theme)
    }

    private fun onTimeChanged() {
        setTimePickerSummary()
    }

    private fun initializeClickListeners() {
        timePicker.setOnPreferenceClickListener {
            TimePickerDialogFragment().show(childFragmentManager, DialogKeys.TIME_PICKER)
            true
        }
        changelog.setOnPreferenceClickListener {
            childFragmentManager.showChangelog()
            true
        }
        messageToDeveloper.setOnPreferenceClickListener {
            context?.openEmailEditor(subject = getString(R.string.subject_message_to_developer))
            true
        }
        filtersHelp.setOnPreferenceClickListener {
            childFragmentManager.showInfoDialog(
                    titleResId = R.string.title_groups_help,
                    contentResId = R.string.content_groups_help,
                    key =  DialogKeys.FILTERS_HELP
            )
            true
        }
        courseIdentifierHelp.setOnPreferenceClickListener {
            childFragmentManager.showInfoDialog(
                    titleResId = R.string.title_course_identifier_help,
                    contentResId = R.string.content_course_identifier_help,
                    key =  DialogKeys.COURSE_IDENTIFIER_HELP
            )
            true
        }
    }

    private fun initializeViews() {
        // hints
        filters.setOnBindEditTextListener { it.hint = getString(R.string.hint_group_highlight) }
        courseIdentifier.setOnBindEditTextListener { it.hint = getString(R.string.hint_course_identifier) }
        // values for list preferences
        scheduleLanguages.entryValues = SCHEDULE_LANGUAGES
        themes.entries = THEME_NAMES_TO_VALUES.keys.map { getString(it) }.toTypedArray()
        themes.entryValues = THEME_NAMES_TO_VALUES.values.map { it.toString() }.toTypedArray()
        // summary providers
        scheduleLanguages.summaryProvider = ListPreferenceSummaryProvider
        themes.summaryProvider = ListPreferenceSummaryProvider
        courseIdentifier.summaryProvider = EditTextPreferenceSummaryProvider
        filters.summaryProvider = EditTextPreferenceSummaryProvider
        // force time summary to apply
        setTimePickerSummary()
    }

    private fun setTimePickerSummary() {
        timePicker.summary = prefsRepo.time.timeFormat()
    }
}