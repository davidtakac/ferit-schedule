package os.dtakac.feritraspored.settings.summaryprovider

import androidx.preference.ListPreference
import androidx.preference.Preference
import os.dtakac.feritraspored.R

object ListPreferenceSummaryProvider : Preference.SummaryProvider<ListPreference> {
    override fun provideSummary(preference: ListPreference): CharSequence {
        val entry = preference.entry
        return if (entry.isEmpty()) {
            preference.context.getString(R.string.placeholder_empty)
        } else {
            entry
        }
    }
}