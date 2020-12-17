package os.dtakac.feritraspored.schedule.viewmodel

import android.net.Uri
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.data.EmailEditorData
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.extensions.isSameWeek
import os.dtakac.feritraspored.common.extensions.scrollFormat
import os.dtakac.feritraspored.common.extensions.urlFormat
import os.dtakac.feritraspored.common.singlelivedata.SingleLiveEvent
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
    val scheduleData = MutableLiveData<ScheduleData>()
    val isLoaderVisible = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val isErrorGone: LiveData<Boolean> = Transformations.map(errorMessage) { it == null }
    val areControlsEnabled: LiveData<Boolean> = Transformations.map(isLoaderVisible) { !it }

    val javascript = SingleLiveEvent<JavascriptData>()
    val openSettings = SingleLiveEvent<Unit>()
    val openInExternalBrowser = SingleLiveEvent<String>()
    val openInCustomTabs = SingleLiveEvent<Uri>()
    val openEmailEditor = SingleLiveEvent<EmailEditorData>()
    val showChangelog = SingleLiveEvent<Unit>()
    val snackBarMessage = SingleLiveEvent<String>()
    val webViewScroll = SingleLiveEvent<ScrollData>()
    val clearWebViewScroll = SingleLiveEvent<Unit>()

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
            showChangelog.call()
        }
    }

    fun onResume() {
        if(!wasLoadedInOnCreate && prefs.isLoadOnResume) {
            loadCurrentWeek()
        }
        wasLoadedInOnCreate = false
    }

    fun onPageDrawn() {
        isLoaderVisible.value = false
        if(buildCurrentDate().isSameWeek(selectedDate)) {
            scrollSelectedDateIntoView()
        }
    }

    fun onUrlClicked(url: String?) {
        val uri = try { Uri.parse(url) } catch (e: Exception) { null }
        if(uri != null) {
            openInCustomTabs.value = uri
        }
    }

    fun onRefreshClicked() {
        if(isOnline()) {
            loadSchedule()
        }
    }

    fun onSettingsClicked() {
        openSettings.call()
    }

    fun onOpenInExternalBrowserClicked() {
        val data = scheduleData.value ?: return
        openInExternalBrowser.value = data.baseUrl
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
                .format()
        //openEmailEditor.value = EmailEditorData(subject, content) todo: fix
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            clearWebViewScroll.call()
            errorMessage.value = null
            isLoaderVisible.value = true

            var error: String? = null
            val data = try {
                getScheduleData()
            } catch (e: Exception) {
                error = res.getString(R.string.template_error_unexpected).format(
                        e.message,
                        prefs.courseIdentifier,
                        selectedDate.urlFormat()
                )
                null
            }

            if(data != null) {
                scheduleData.value = data
            } else {
                errorMessage.value = error
                isLoaderVisible.value = false
            }
        }
    }

    private fun scrollSelectedDateIntoView() {
        val scrollJs = res
                .readFromAssets("template_scroll_into_view.js")
                .format(selectedDate.scrollFormat())
        javascript.value = JavascriptData(js = scrollJs, callback = { postScrollEvent(it) })
    }

    private fun postScrollEvent(elementPosition: String) {
        val elementPositionDp = elementPosition.toFloatOrNull()
        if(elementPositionDp != null) {
            webViewScroll.value = ScrollData(
                    speed = scrollPixelsPerMs,
                    verticalPosition = res.toPx(elementPositionDp).roundToInt(),
                    interpolator = scrollInterpolator
            )
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
                    errorMessage.value = res.getString(R.string.error_no_network)
                } else {
                    snackBarMessage.value = res.getString(R.string.notify_no_network)
                }
            } else {
                snackBarMessage.value = res.getString(R.string.notify_no_network)
            }
        }
        return isOnline
    }

    private suspend fun getScheduleData() = scheduleRepository.getScheduleData(
                scheduleUrl = prefs.scheduleTemplate.format(
                        selectedDate.urlFormat(),
                        prefs.courseIdentifier
                ),
                showTimeOnBlocks = prefs.isShowTimeOnBlocks,
                filters = if (!prefs.areFiltersEnabled) {
                    listOf()
                } else {
                    prefs.filters
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.filterNot { it.isEmpty() || it.isBlank() }
                            ?: listOf()
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