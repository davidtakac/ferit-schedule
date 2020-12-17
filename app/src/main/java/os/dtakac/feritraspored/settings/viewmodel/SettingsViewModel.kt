package os.dtakac.feritraspored.settings.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.common.constants.SharedPreferenceKeys
import os.dtakac.feritraspored.common.extensions.timeFormat
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.singlelivedata.SingleLiveEvent

class SettingsViewModel(
        private val prefs: PreferenceRepository
): ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    val timePickerSummary = MutableLiveData<String>()
    val theme = MutableLiveData<Int>()
    val showTimePicker = SingleLiveEvent<Unit>()
    val showChangelog = SingleLiveEvent<Unit>()
    val showFiltersHelp = SingleLiveEvent<Unit>()
    val showCourseIdentifierHelp = SingleLiveEvent<Unit>()
    val openMessageToDevEditor = SingleLiveEvent<Unit>()

    init {
        setTimePickerSummary()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key != SharedPreferenceKeys.THEME && key != SharedPreferenceKeys.SETTINGS_MODIFIED) {
            prefs.isReloadToApplySettings = true
        }
        when(key) {
            SharedPreferenceKeys.THEME -> onThemeChanged()
            SharedPreferenceKeys.TIME_PICKER -> onTimeChanged()
        }
    }

    fun onResume() {
        prefs.registerListener(this)
    }

    fun onPause() {
        prefs.unregisterListener(this)
    }

    private fun onThemeChanged() {
        setTheme()
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
        openMessageToDevEditor.call()
    }

    private fun setTheme() {
        theme.value = prefs.theme
    }

    private fun setTimePickerSummary() {
        timePickerSummary.value = prefs.time.timeFormat()
    }
}