package os.dtakac.feritraspored.schedule.view_model

import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.*
import os.dtakac.feritraspored.BuildConfig
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.data.EmailEditorData
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
    val scheduleData = MutableLiveData<Event<ScheduleData>>()
    val title = MutableLiveData(res.getString(R.string.label_schedule))
    val javascript = MutableLiveData<Event<JavascriptData>>()
    val isLoaderVisible = MutableLiveData<Event<Boolean>>()
    val openSettings = MutableLiveData<Event<Unit>>()
    val openInExternalBrowser = MutableLiveData<Event<String>>()
    val openInCustomTabs = MutableLiveData<Event<String>>()
    val openEmailEditor = MutableLiveData<Event<EmailEditorData>>()
    val showChangelog = MutableLiveData<Event<Unit>>()
    val errorMessage = MutableLiveData<Event<String?>>()
    val snackBarMessage = MutableLiveData<Event<String>>()
    val webViewScroll = MutableLiveData<Event<ScrollData>>()
    val isErrorGone: LiveData<Event<Boolean>> = Transformations.map(errorMessage) {
        Event(it.peekContent() == null)
    }
    val areControlsEnabled: LiveData<Event<Boolean>> = Transformations.map(isLoaderVisible) {
        Event(!it.peekContent())
    }

    private var isNightMode: Boolean = false
    private var selectedDate = LocalDate.MIN
    private val scrollPixelsPerMs by lazy { res.toPx(dp = 2.8f).toDouble() }
    private val scrollInterpolator by lazy { DecelerateInterpolator(2.5f) }

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
        val subject = res.getString(R.string.subject_bug_report)
        val content = res
                .getString(R.string.template_bug_report)
                .format(errorMessage.peekContent() ?: "")
        openEmailEditor.postEvent(EmailEditorData(subject, content))
    }
    //endregion

    //region Helper methods
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
        val scrollJs = res
                .readFromAssets("template_scroll_into_view.js")
                .format(selectedDate.scrollFormat())
        javascript.postEvent(JavascriptData(js = scrollJs, callback = { postScrollEvent(it) }))
    }

    private fun postScrollEvent(elementPosition: String) {
        val elementPositionDp = elementPosition.toFloatOrNull()
        if(elementPositionDp != null) {
            webViewScroll.postEvent(ScrollData(
                    speed = scrollPixelsPerMs,
                    verticalPosition = res.toPx(elementPositionDp).roundToInt(),
                    interpolator = scrollInterpolator
            ))
        }
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
                if(errorMessage.peekContent() == null) {
                    errorMessage.postEvent(res.getString(R.string.error_no_network))
                } else {
                    /* If the user is persistent in spamming the controls even when the error
                       screen is showing, notify him of his ignorance. */
                    snackBarMessage.postEvent(res.getString(R.string.notify_no_network))
                }
            } else {
                snackBarMessage.postEvent(res.getString(R.string.notify_no_network))
            }
        }
        return isOnline
    }

    private suspend fun getScheduleData() = scheduleRepository.getScheduleData(
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