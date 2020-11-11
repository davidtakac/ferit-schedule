package os.dtakac.feritraspored.settings.view

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.DIALOG_COURSE_IDENTIFIER_HELP
import os.dtakac.feritraspored.common.constants.DIALOG_FILTERS_HELP
import os.dtakac.feritraspored.common.constants.DIALOG_TIME_PICKER
import os.dtakac.feritraspored.common.constants.DIALOG_WHATS_NEW
import os.dtakac.feritraspored.common.utils.bugReportIntent
import os.dtakac.feritraspored.common.utils.preference
import os.dtakac.feritraspored.settings.view_model.SettingsViewModel
import os.dtakac.feritraspored.views.dialog_info.InfoDialogFragment
import os.dtakac.feritraspored.views.dialog_time_picker.TimePickerDialogFragment

class SettingsFragment : PreferenceFragmentCompat() {
    private val themes: ListPreference by preference(R.string.key_theme)
    private val filters: EditTextPreference by preference(R.string.key_filters)
    private val courseIdentifier: EditTextPreference by preference(R.string.key_course_identifier)
    private val filtersHelp: Preference by preference(R.string.key_filters_help)
    private val timePicker: Preference by preference(R.string.key_time_picker)
    private val changelog: Preference by preference(R.string.key_changelog)
    private val bugReport: Preference by preference(R.string.key_report_bug)
    private val courseIdentifierHelp: Preference by preference(R.string.key_course_identifier_help)

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeClickListeners()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun initializeClickListeners() {
        timePicker.setOnPreferenceClickListener { showTimePicker() }
        changelog.setOnPreferenceClickListener { showChangelog() }
        bugReport.setOnPreferenceClickListener { showBugReport() }
        filtersHelp.setOnPreferenceClickListener { showFiltersHelp() }
        courseIdentifierHelp.setOnPreferenceClickListener { showCourseIdentifierHelp() }
    }

    private fun initializeObservers() {

    }

    private fun showTimePicker(): Boolean {
        TimePickerDialogFragment().show(childFragmentManager, DIALOG_TIME_PICKER)
        return true
    }

    private fun showChangelog(): Boolean {
        showInfoDialog(
                titleResId = R.string.title_whats_new,
                contentResId = R.string.content_whats_new,
                dismissResId = R.string.dismiss_whats_new,
                key = DIALOG_WHATS_NEW
        )
        return true
    }

    private fun showBugReport(): Boolean {
        startActivity(bugReportIntent(resources))
        return true
    }

    private fun showFiltersHelp(): Boolean {
        showInfoDialog(
                titleResId = R.string.title_groups_help,
                contentResId = R.string.content_groups_help,
                key = DIALOG_FILTERS_HELP
        )
        return true
    }

    private fun showCourseIdentifierHelp(): Boolean {
        showInfoDialog(
                titleResId = R.string.title_course_identifier_help,
                contentResId = R.string.content_course_identifier_help,
                key = DIALOG_COURSE_IDENTIFIER_HELP
        )
        return true
    }

    private fun showInfoDialog(
            @StringRes titleResId: Int,
            @StringRes contentResId: Int,
            @StringRes dismissResId: Int = R.string.okay,
            key: String
    ) {
        val infoDialog = InfoDialogFragment.newInstance(titleResId, contentResId, dismissResId)
        infoDialog.show(childFragmentManager, key)
    }
}