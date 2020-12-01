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
import os.dtakac.feritraspored.schedule.data.ScrollData
import os.dtakac.feritraspored.schedule.repository.ScheduleRepository
import java.lang.Exception
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
    val isLoaderVisible = MutableLiveData<Event<Boolean>>()
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
    val controlsEnabled: LiveData<Event<Boolean>> = Transformations.map(isLoaderVisible) {
        Event(!it.peekContent())
    }
    val scrollToPositionOffset = MutableLiveData<Event<ScrollData>>()
    //endregion

    //region Private variables
    private var isNightMode: Boolean = false
    private var selectedDate = LocalDate.MIN
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
            selectedDate = buildCurrentDate()
            if(isOnline()) {
                loadSchedule()
            }
        } else if(prefs.isLoadOnResume) {
            onCurrentWeekClicked()
        }
    }
    //endregion

    //region Event handling
    fun onPageFinished() {
        if(buildCurrentDate().isSameWeek(selectedDate)) {
            scrollSelectedDateIntoView()
        }
    }

    fun onUrlClicked(url: String?) {
        if(url != null) {
            openInCustomTabs.postEvent(url)
        }
    }

    fun onRefreshClicked() {
        if(selectedDate == LocalDate.MIN) {
            selectedDate = buildCurrentDate()
        }
        if(isOnline()) {
            loadSchedule()
        }
    }

    fun onSettingsClicked() {
        openSettings.postEvent()
    }

    fun onOpenInExternalBrowserClicked() {
        val data = scheduleData.peekContent() ?: return
        openInExternalBrowser.postEvent(data.baseUrl)
    }

    fun onPreviousWeekClicked() {
        if(isOnline() && selectedDate != LocalDate.MIN) {
            selectedDate = selectedDate.minusWeeks(1)
            loadSchedule()
        }
    }

    fun onCurrentWeekClicked() {
        val currentDate = buildCurrentDate()
        if(currentDate.isSameWeek(selectedDate) && scheduleData.value != null) {
            scrollSelectedDateIntoView()
        } else if(isOnline()) {
            selectedDate = currentDate
            loadSchedule()
        }
    }

    fun onNextWeekClicked() {
        if(isOnline() && selectedDate != LocalDate.MIN) {
            selectedDate = selectedDate.plusWeeks(1)
            loadSchedule()
        }
    }

    fun onBugReportClicked() {
        openBugReport.postEvent(res.getString(R.string.template_bug_report).format(
                errorMessage.peekContent() ?: ""
        ))
    }
    //endregion

    //region Private helper methods
    private fun loadSchedule() {
        viewModelScope.launch {
            errorMessage.postEvent(null)
            isLoaderVisible.postEvent(true)

            var error: String? = null
            val data = try {
                getScheduleData()
            } catch (e: Exception) {
                error = res.getString(R.string.template_error_unexpected).format(e.message)
                null
            }

            if(data != null) {
                scheduleData.postEvent(data)
            } else {
                errorMessage.postEvent(error)
            }
            isLoaderVisible.postEvent(false)
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
                        scrollToPositionOffset.postEvent(ScrollData(
                                //speed obtained with trial and error, it seemed the prettiest
                                pixelsPerMillisecond = 8,
                                positionInPixels = px
                        ))
                    }
                }
        ))
    }

    private fun buildCurrentDate(): LocalDate {
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

    private fun isOnline(): Boolean {
        val isOnline = res.isOnline()
        if(!isOnline) {
            if(scheduleData.value == null) {
                errorMessage.postEvent(res.getString(R.string.error_no_network))
            } else {
                snackBarMessage.postEvent(res.getString(R.string.notify_no_network))
            }
        }
        return isOnline
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