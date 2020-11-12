package os.dtakac.feritraspored.schedule.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.scripts.ScriptProvider
import java.time.LocalDate

class ScheduleViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository,
        private val scriptProvider: ScriptProvider
): ViewModel() {
    val url = MutableLiveData<String>()

    private var selectedDate: LocalDate = nowDate
    private val nowDate: LocalDate
        get() = LocalDate.now()

    fun onResume() {
        url.postValue(buildUrl())
    }

    private fun buildUrl(): String {
        return res.getString(R.string.template_schedule).format(
                selectedDate.toString(),
                prefs.courseIdentifier
        )
    }
}