package os.dtakac.feritraspored.settings.view_model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.common.preferences.PreferenceRepository

class SettingsViewModel(
        private val prefsRepository: PreferenceRepository
): ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {

    }

    fun onResume() {
        prefsRepository.registerListener(this)
    }

    fun onPause() {
        prefsRepository.unregisterListener(this)
    }
}