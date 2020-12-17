package os.dtakac.feritraspored.settings.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.*
import os.dtakac.feritraspored.common.extensions.openEmailEditor
import os.dtakac.feritraspored.common.extensions.preference
import os.dtakac.feritraspored.common.extensions.showChangelog
import os.dtakac.feritraspored.common.extensions.showInfoDialog
import os.dtakac.feritraspored.settings.viewmodel.PreferenceViewModel
import os.dtakac.feritraspored.common.view.dialog_time_picker.TimePickerDialogFragment

@Suppress("unused")
class SettingsFragment : PreferenceFragmentCompat() {
    private val themes: ListPreference by preference(SharedPreferenceKeys.THEME)
    private val filters: EditTextPreference by preference(SharedPreferenceKeys.FILTERS)
    private val courseIdentifier: EditTextPreference by preference(SharedPreferenceKeys.IDENTIFIER)
    private val filtersHelp: Preference by preference(SharedPreferenceKeys.FILTERS_HELP)
    private val timePicker: Preference by preference(SharedPreferenceKeys.TIME_PICKER)
    private val changelog: Preference by preference(SharedPreferenceKeys.CHANGELOG)
    private val messageToDeveloper: Preference by preference(SharedPreferenceKeys.DEV_MESSAGE)
    private val courseIdentifierHelp: Preference by preference(SharedPreferenceKeys.IDENTIFIER_HELP)
    private val scheduleLanguages: ListPreference by preference(SharedPreferenceKeys.SCHEDULE_LANG)

    private val viewModel: PreferenceViewModel by viewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeClickListeners()
        initializeViews()
        initializeObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    private fun initializeClickListeners() {
        timePicker.setOnPreferenceClickListener {
            viewModel.onTimePickerClicked(); true
        }
        changelog.setOnPreferenceClickListener {
            viewModel.onChangelogClicked(); true
        }
        messageToDeveloper.setOnPreferenceClickListener {
            viewModel.onMessageToDeveloperClicked(); true
        }
        filtersHelp.setOnPreferenceClickListener {
            viewModel.onFiltersHelpClicked(); true
        }
        courseIdentifierHelp.setOnPreferenceClickListener {
            viewModel.onCourseIdentifierHelpClicked(); true
        }
    }

    private fun initializeViews() {
        filters.setOnBindEditTextListener {
            it.hint = resources.getString(R.string.hint_group_highlight)
        }
        courseIdentifier.setOnBindEditTextListener {
            it.hint = resources.getString(R.string.hint_course_identifier)
        }
        themes.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        scheduleLanguages.entryValues = SCHEDULE_LANGUAGES
        scheduleLanguages.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }

    private fun initializeObservers() {
        viewModel.timePickerSummary.observe(viewLifecycleOwner) {
            timePicker.summary = it
        }
        viewModel.timePickerEnabled.observe(viewLifecycleOwner) {
            timePicker.isEnabled = it
        }
        viewModel.filtersSummary.observe(viewLifecycleOwner) {
            filters.summary = it
        }
        viewModel.filtersEnabled.observe(viewLifecycleOwner) {
            filters.isEnabled = it
            filtersHelp.isEnabled = it
        }
        viewModel.courseIdentifierSummary.observe(viewLifecycleOwner) {
            courseIdentifier.summary = it
        }
        viewModel.theme.observe(viewLifecycleOwner) {
            AppCompatDelegate.setDefaultNightMode(it)
        }
        viewModel.themeOptionsHuman.observe(viewLifecycleOwner) {
            themes.entries = it
        }
        viewModel.themeOptions.observe(viewLifecycleOwner) {
            themes.entryValues = it
        }
        viewModel.showTimePicker.observe(viewLifecycleOwner) {
            TimePickerDialogFragment().show(childFragmentManager, DIALOG_TIME_PICKER)
        }
        viewModel.showChangelog.observe(viewLifecycleOwner) {
            childFragmentManager.showChangelog()
        }
        viewModel.showFiltersHelp.observe(viewLifecycleOwner) {
            childFragmentManager.showInfoDialog(
                    titleResId = R.string.title_groups_help,
                    contentResId = R.string.content_groups_help,
                    key = DIALOG_FILTERS_HELP
            )
        }
        viewModel.showCourseIdentifierHelp.observe(viewLifecycleOwner) {
            childFragmentManager.showInfoDialog(
                    titleResId = R.string.title_course_identifier_help,
                    contentResId = R.string.content_course_identifier_help,
                    key = DIALOG_COURSE_IDENTIFIER_HELP
            )
        }
        viewModel.openEmailEditor.observe(viewLifecycleOwner) {
            context?.openEmailEditor(it)
        }
    }
}