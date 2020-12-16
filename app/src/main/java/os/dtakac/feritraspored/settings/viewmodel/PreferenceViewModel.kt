package os.dtakac.feritraspored.settings.viewmodel

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.data.EmailEditorData
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.singlelivedata.SingleLiveEvent

class PreferenceViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository
): ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    val timePickerSummary = MutableLiveData<String>()
    val timePickerEnabled = MutableLiveData<Boolean>()
    val filtersSummary = MutableLiveData<String>()
    val filtersEnabled = MutableLiveData<Boolean>()
    val courseIdentifierSummary = MutableLiveData<String>()
    val themeOptionsHuman = MutableLiveData<Array<CharSequence>>()
    val themeOptions = MutableLiveData<Array<CharSequence>>()
    val theme = MutableLiveData<Int>()
    val showTimePicker = SingleLiveEvent<Unit>()
    val showChangelog = SingleLiveEvent<Unit>()
    val showFiltersHelp = SingleLiveEvent<Unit>()
    val showCourseIdentifierHelp = SingleLiveEvent<Unit>()
    val openEmailEditor = SingleLiveEvent<EmailEditorData>()

    init {
        setThemePickerValues()
        setTimePickerSummary()
        setTimePickerEnabled()
        setFiltersSummary()
        setFiltersEnabled()
        setCourseIdentifierSummary()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key != res.getString(R.string.key_theme) && key != res.getString(R.string.key_settings_modified)) {
            prefs.isReloadToApplySettings = true
        }
        when(key) {
            res.getString(R.string.key_skip_day) -> onSkipDayChanged()
            res.getString(R.string.key_filters) -> onFiltersChanged()
            res.getString(R.string.key_filters_toggle) -> onFiltersToggled()
            res.getString(R.string.key_theme) -> onThemeChanged()
            res.getString(R.string.key_course_identifier) -> onCourseIdentifierChanged()
            res.getString(R.string.key_time_hour),
            res.getString(R.string.key_time_minute) -> onTimeChanged()
        }
    }

    fun onResume() {
        prefs.registerListener(this)
    }

    fun onPause() {
        prefs.unregisterListener(this)
    }

    private fun onSkipDayChanged() {
        setTimePickerEnabled()
    }

    private fun onFiltersChanged() {
        setFiltersSummary()
    }

    private fun onFiltersToggled() {
        setFiltersEnabled()
    }

    private fun onThemeChanged() {
        setTheme()
    }

    private fun onCourseIdentifierChanged() {
        setCourseIdentifierSummary()
    }

    private fun onTimeChanged() {
        setTimePickerSummary()
    }

    fun onTimePickerClicked() {
        showTimePicker.call()
    }

    fun onChangelogClicked() {
        showChangelog.call()
    }

    fun onFiltersHelpClicked() {
        showFiltersHelp.call()
    }

    fun onCourseIdentifierHelpClicked() {
        showCourseIdentifierHelp.call()
    }

    fun onMessageToDeveloperClicked() {
        val subject = res.getString(R.string.subject_message_to_developer)
        openEmailEditor.value = EmailEditorData(subject = subject)
    }

    private fun setTimePickerEnabled() {
        timePickerEnabled.value = prefs.isSkipDay
    }

    private fun setFiltersSummary() {
        val filters = prefs.filters
        filtersSummary.value = if(filters.isNullOrEmpty()) {
            res.getString(R.string.placeholder_empty)
        } else {
            filters
        }
    }

    private fun setFiltersEnabled() {
        filtersEnabled.value = prefs.areFiltersEnabled
    }

    private fun setTheme() {
        theme.value = prefs.theme
    }

    private fun setCourseIdentifierSummary() {
        val courseIdentifier = prefs.courseIdentifier
        courseIdentifierSummary.value = if(courseIdentifier.isNullOrEmpty()) {
            res.getString(R.string.placeholder_empty)
        } else {
            courseIdentifier
        }
    }

    private fun setTimePickerSummary() {
        timePickerSummary.value = formatTime(prefs.timeHour, prefs.timeMinute)
    }

    private fun setThemePickerValues() {
        themeOptionsHuman.value = arrayOf(
                res.getString(R.string.theme_option_system),
                res.getString(R.string.theme_option_light),
                res.getString(R.string.theme_option_dark)
        )
        themeOptions.value = arrayOf(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString(),
                AppCompatDelegate.MODE_NIGHT_NO.toString(),
                AppCompatDelegate.MODE_NIGHT_YES.toString()
        )
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val hourStr = (if (hour < 10) "0" else "") + hour
        val minStr = (if (minute < 10) "0" else "") + minute
        return "$hourStr:$minStr"
    }
}