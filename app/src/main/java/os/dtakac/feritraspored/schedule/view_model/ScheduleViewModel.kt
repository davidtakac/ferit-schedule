package os.dtakac.feritraspored.schedule.view_model

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.scripts.ScriptProvider
import os.dtakac.feritraspored.common.utils.isSameWeek
import java.time.LocalDate
import java.time.LocalTime

class ScheduleViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository,
        private val scriptProvider: ScriptProvider
): ViewModel() {
    val url = MutableLiveData<String>()
    val javascript = MutableLiveData<String>()
    val loaderVisibility = MutableLiveData<Int>()

    private var selectedDate: LocalDate = nowDate
    private val nowDate: LocalDate
        get() = LocalDate.now()
    private val nowTime: LocalTime
        get() = LocalTime.now()

    fun onResume() {
        url.postValue(buildUrl())
    }

    fun onLoadingStarted() {
        loaderVisibility.postValue(View.VISIBLE)
    }

    fun onLoadingFinished() {
        javascript.postValue(buildJavascript())
    }

    fun onJavascriptFinished() {
        loaderVisibility.postValue(View.GONE)
    }

    private fun buildUrl(): String {
        return res.getString(R.string.template_schedule).format(
                selectedDate.toString(),
                prefs.courseIdentifier
        )
    }

    private fun buildJavascript(): String {
        var js = scriptProvider.hideJunkFunction() + scriptProvider.timeOnBlocksFunction()
        if(res.isNightMode()) {
            js += scriptProvider.darkThemeFunction()
        }
        if(prefs.isFiltersEnabled) {
            val filtersTrimmed = prefs.filters?.split(",")?.map { it.trim() } ?: listOf()
            js += scriptProvider.highlightBlocksFunction(filtersTrimmed)
        }
        if(nowDate.isSameWeek(selectedDate)) {
            //todo: scroll to current day
        }
        return js
    }
}