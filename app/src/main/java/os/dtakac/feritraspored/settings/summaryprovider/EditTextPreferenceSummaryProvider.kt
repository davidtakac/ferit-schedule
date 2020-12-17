package os.dtakac.feritraspored.settings.summaryprovider

import androidx.preference.EditTextPreference
import androidx.preference.Preference
import os.dtakac.feritraspored.R

object EditTextPreferenceSummaryProvider : Preference.SummaryProvider<EditTextPreference> {
    override fun provideSummary(preference: EditTextPreference): CharSequence {
        val entry = preference.text
        return if (entry.isNullOrEmpty()) {
            preference.context.getString(R.string.placeholder_empty)
        } else {
            entry
        }
    }
}