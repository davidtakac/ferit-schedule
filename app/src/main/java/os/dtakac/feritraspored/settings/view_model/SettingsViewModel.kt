package os.dtakac.feritraspored.settings.view_model

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.data.EmailEditorData
import os.dtakac.feritraspored.common.event.Event
import os.dtakac.feritraspored.common.event.postEvent
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository

class SettingsViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository
): ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    val timePickerSummary = MutableLiveData<String>()
    val timePickerEnabled = MutableLiveData<Boolean>()
    val filtersSummary = MutableLiveData<String>()
    val filtersEnabled = MutableLiveData<Boolean>()
    val courseIdentifierSummary = MutableLiveData<String>()
    val theme = MutableLiveData<Int>()
    val showTimePicker = MutableLiveData<Event<Unit>>()
    val showChangelog = MutableLiveData<Event<Unit>>()
    val showFiltersHelp = MutableLiveData<Event<Unit>>()
    val showCourseIdentifierHelp = MutableLiveData<Event<Unit>>()
    val openEmailEditor = MutableLiveData<Event<EmailEditorData>>()

    init {
        setTimePickerSummary()
        setTimePickerEnabled()
        setFiltersSummary()
        setFiltersEnabled()
        setCourseIdentifierSummary()
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        this.prefs.isSettingsModified = true
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
        showTimePicker.postEvent()
    }

    fun onChangelogClicked() {
        showChangelog.postEvent()
    }

    fun onFiltersHelpClicked() {
        showFiltersHelp.postEvent()
    }

    fun onCourseIdentifierHelpClicked() {
        showCourseIdentifierHelp.postEvent()
    }

    fun onMessageToDeveloperClicked() {
        val subject = res.getString(R.string.subject_message_to_developer)
        openEmailEditor.postEvent(EmailEditorData(subject = subject))
    }

    private fun setTimePickerEnabled() {
        timePickerEnabled.postValue(prefs.isSkipDay)
    }

    private fun setFiltersSummary() {
        val filters = prefs.filters
        filtersSummary.postValue(
                if(filters.isNullOrEmpty()) {
                    res.getString(R.string.placeholder_empty)
                } else {
                    filters
                }
        )
    }

    private fun setFiltersEnabled() {
        filtersEnabled.postValue(prefs.isFiltersEnabled)
    }

    private fun setTheme() {
        theme.postValue(prefs.theme)
    }

    private fun setCourseIdentifierSummary() {
        val courseIdentifier = prefs.courseIdentifier
        courseIdentifierSummary.postValue(
                if(courseIdentifier.isNullOrEmpty()) {
                    res.getString(R.string.placeholder_empty)
                } else {
                    courseIdentifier
                }
        )
    }

    private fun setTimePickerSummary() {
        timePickerSummary.postValue(formatTime(prefs.timeHour, prefs.timeMinute))
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val hourStr = (if (hour < 10) "0" else "") + hour
        val minStr = (if (minute < 10) "0" else "") + minute
        return "$hourStr:$minStr"
    }
}