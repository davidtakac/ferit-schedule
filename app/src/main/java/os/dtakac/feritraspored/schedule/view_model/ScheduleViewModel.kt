package os.dtakac.feritraspored.schedule.view_model

import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.data.EmailEditorData
import os.dtakac.feritraspored.common.event.Event
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
    //normal live data
    val scheduleData = MutableLiveData<ScheduleData>()
    val isLoaderVisible = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val isErrorGone: LiveData<Boolean> = Transformations.map(errorMessage) { it == null }
    val areControlsEnabled: LiveData<Boolean> = Transformations.map(isLoaderVisible) { !it }
    //event live data
    val javascript = MutableLiveData<Event<JavascriptData>>()
    val openSettings = MutableLiveData<Event<Unit>>()
    val openInExternalBrowser = MutableLiveData<Event<String>>()
    val openInCustomTabs = MutableLiveData<Event<String>>()
    val openEmailEditor = MutableLiveData<Event<EmailEditorData>>()
    val showChangelog = MutableLiveData<Event<Unit>>()
    val snackBarMessage = MutableLiveData<Event<String>>()
    val webViewScroll = MutableLiveData<Event<ScrollData>>()
    val clearWebViewScroll = MutableLiveData<Event<Unit>>()

    private var wasLoadedInOnCreate = false
    private var selectedDate = buildCurrentDate()
    private val scrollPixelsPerMs by lazy { res.toPx(dp = 2.2f).toDouble() }
    private val scrollInterpolator by lazy { DecelerateInterpolator(2.5f) }

    fun onViewCreated() {
        if(isOnline() && (scheduleData.value == null || prefs.isReloadToApplySettings)) {
            wasLoadedInOnCreate = true
            selectedDate = buildCurrentDate()
            loadSchedule()
        }
        if(res.getBoolean(R.bool.showChangelog) && prefs.version < BuildConfig.VERSION_CODE) {
            showChangelog.postEvent()
        }
    }

    fun onResume() {
        if(!wasLoadedInOnCreate && prefs.isLoadOnResume) {
            loadCurrentWeek()
        }
        wasLoadedInOnCreate = false
    }

    fun onPageDrawn() {
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
        if(isOnline()) {
            loadSchedule()
        }
    }

    fun onSettingsClicked() {
        openSettings.postEvent()
    }

    fun onOpenInExternalBrowserClicked() {
        val data = scheduleData.value ?: return
        openInExternalBrowser.postEvent(data.baseUrl)
    }

    fun onPreviousWeekClicked() {
        if(isOnline()) {
            selectedDate = selectedDate.minusWeeks(1)
            loadSchedule()
        }
    }

    fun onCurrentWeekClicked() {
        loadCurrentWeek()
    }

    fun onNextWeekClicked() {
        if(isOnline()) {
            selectedDate = selectedDate.plusWeeks(1)
            loadSchedule()
        }
    }

    fun onBugReportClicked() {
        val subject = res.getString(R.string.subject_bug_report)
        val content = res
                .getString(R.string.template_bug_report)
                .format(errorMessage.value ?: "")
        openEmailEditor.postEvent(EmailEditorData(subject, content))
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            clearWebViewScroll.postEvent()
            errorMessage.postValue(null)
            isLoaderVisible.postValue(true)

            var error: String? = null
            val data = try {
                getScheduleData()
            } catch (e: Exception) {
                error = res.getString(R.string.template_error_unexpected).format(e.message)
                null
            }

            if(data != null) {
                scheduleData.postValue(data)
            } else {
                errorMessage.postValue(error)
            }
            isLoaderVisible.postValue(false)
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
        if(newSelectedDate.dayOfWeek == DayOfWeek.SATURDAY && prefs.isSkipSaturday) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        if(newSelectedDate.dayOfWeek == DayOfWeek.SUNDAY) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        if(prefs.isSkipDay && LocalTime.now() > LocalTime.of(prefs.timeHour, prefs.timeMinute)) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        return newSelectedDate
    }

    private fun isOnline(): Boolean {
        val isOnline = res.isOnline()
        if(!isOnline) {
            if(scheduleData.value == null) {
                if(errorMessage.value == null) {
                    errorMessage.postValue(res.getString(R.string.error_no_network))
                } else {
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
                filters = if (!prefs.areFiltersEnabled) {
                    listOf()
                } else {
                    prefs.filters?.split(",")?.map { it.trim() } ?: listOf()
                }
        )

    private fun loadCurrentWeek() {
        val currentDate = buildCurrentDate()
        if(currentDate.isSameWeek(selectedDate) && scheduleData.value != null) {
            scrollSelectedDateIntoView()
        } else if(isOnline()) {
            selectedDate = currentDate
            loadSchedule()
        }
    }
}