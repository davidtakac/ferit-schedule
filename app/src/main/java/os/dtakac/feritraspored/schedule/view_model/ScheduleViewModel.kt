package os.dtakac.feritraspored.schedule.view_model

import android.view.View
import androidx.lifecycle.*
import os.dtakac.feritraspored.BuildConfig
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.event.Event
import os.dtakac.feritraspored.common.event.peekContent
import os.dtakac.feritraspored.common.event.postEvent
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.extensions.isSameWeek
import os.dtakac.feritraspored.common.extensions.scrollFormat
import os.dtakac.feritraspored.schedule.data.JavascriptData
import os.dtakac.feritraspored.schedule.data.ScheduleData
import os.dtakac.feritraspored.schedule.repository.ScheduleRepository
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

class ScheduleViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository,
        private val scheduleRepository: ScheduleRepository
): ViewModel() {
    //region Live data
    val scheduleData = MutableLiveData<Event<ScheduleData>>()
    val title = MutableLiveData(res.getString(R.string.label_schedule))
    val javascript = MutableLiveData<Event<JavascriptData>>()
    val loaderVisibility = MutableLiveData<Event<Int>>()
    val openSettings = MutableLiveData<Event<Unit>>()
    val openInExternalBrowser = MutableLiveData<Event<String>>()
    val openInCustomTabs = MutableLiveData<Event<String>>()
    val openBugReport = MutableLiveData<Event<String>>()
    val showChangelog = MutableLiveData<Event<Unit>>()
    val errorMessage = MutableLiveData<Event<String?>>()
    val errorVisibility: LiveData<Event<Int>> = Transformations.map(errorMessage) {
        Event(if(it.peekContent() == null) View.GONE else View.VISIBLE)
    }
    val snackBarMessage = MutableLiveData<Event<String>>()
    val controlsEnabled: LiveData<Event<Boolean>> = Transformations.map(loaderVisibility) {
        Event(it.peekContent() == View.GONE)
    }
    val scrollToPositionOffset = MutableLiveData<Event<Int>>()
    //endregion

    //region Private variables
    private var isNightMode: Boolean = false
    private var selectedDate = LocalDate.MIN
        set(value) {
            field = value
            startUrl()
        }
    //endregion

    //region Lifecycle
    fun onResume(
            loadedUrl: String?,
            currentNightMode: Boolean
    ) {
        if(prefs.version < BuildConfig.VERSION_CODE) {
            showChangelog.postEvent()
        }
        if( isNightMode != currentNightMode ||
            prefs.isSettingsModified ||
            loadedUrl == null
        ) {
            isNightMode = currentNightMode
            selectedDate = buildCurrentWeek()
        } else if(prefs.isLoadOnResume) {
            onCurrentWeekClicked()
        }
    }
    //endregion

    //region Event handling
    fun onPageFinished() {
        if(buildCurrentWeek().isSameWeek(selectedDate)) {
            scrollSelectedDateIntoView()
        }
    }

    fun onUrlClicked(url: String?) {
        if(url == null) return
        openInCustomTabs.postEvent(url)
    }

    fun onRefreshClicked() {
        startUrl()
    }

    fun onSettingsClicked() {
        openSettings.postEvent()
    }

    fun onOpenInExternalBrowserClicked() {
        val data = scheduleData.peekContent() ?: return
        openInExternalBrowser.postEvent(data.baseUrl)
    }

    fun onPreviousWeekClicked() {
        selectedDate = selectedDate.minusWeeks(1)
    }

    fun onCurrentWeekClicked() {
        val currentWeek = buildCurrentWeek()
        if(currentWeek.isSameWeek(selectedDate)) {
            scrollSelectedDateIntoView()
        } else {
            selectedDate = currentWeek
        }
    }

    fun onNextWeekClicked() {
        selectedDate = selectedDate.plusWeeks(1)
    }

    fun onBugReportClicked() {
        openBugReport.postEvent(res.getString(R.string.template_bug_report).format(
                errorMessage.peekContent() ?: ""
        ))
    }
    //endregion

    //region Private helper methods
    private fun startUrl() {
        viewModelScope.launch {
            if(!res.isOnline()) {
                snackBarMessage.postEvent(res.getString(R.string.notify_no_network))
                return@launch
            }
            errorMessage.postEvent(null)
            loaderVisibility.postEvent(View.VISIBLE)
            val data = try {
                getScheduleData()
            } catch (e: IOException) {
                errorMessage.postEvent(
                        res.getString(R.string.template_error_unexpected).format(e.message)
                )
                loaderVisibility.postEvent(View.GONE)
                return@launch
            }
            scheduleData.postEvent(data)
            loaderVisibility.postEvent(View.GONE)
        }
    }

    private fun scrollSelectedDateIntoView() {
        val scrollJs = res.readFromAssets("template_scroll_into_view.js")
                .format(selectedDate.scrollFormat())

        javascript.postEvent(JavascriptData(
                javascript = scrollJs,
                valueListener = {
                    val dp = it.toFloatOrNull()
                    if(dp != null) {
                        val px = res.toPx(dp).roundToInt()
                        scrollToPositionOffset.postEvent(px)
                    }
                }
        ))
    }

    private fun buildCurrentWeek(): LocalDate {
        var newSelectedDate = LocalDate.now()
        if(prefs.isSkipDay && LocalTime.now() > LocalTime.of(prefs.timeHour, prefs.timeMinute)) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        if(newSelectedDate.dayOfWeek == DayOfWeek.SATURDAY && prefs.isSkipSaturday) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        if(newSelectedDate.dayOfWeek == DayOfWeek.SUNDAY) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        return newSelectedDate
    }

    private suspend fun getScheduleData(): ScheduleData = scheduleRepository.getScheduleData(
                withDate = selectedDate,
                courseIdentifier = prefs.courseIdentifier ?: "",
                showTimeOnBlocks = prefs.isShowTimeOnBlocks,
                filters = if (!prefs.isFiltersEnabled) {
                    listOf()
                } else {
                    prefs.filters?.split(",")?.map { it.trim() } ?: listOf()
                },
                applyDarkTheme = isNightMode
        )
    //endregion
}