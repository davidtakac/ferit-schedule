package os.dtakac.feritraspored.schedule.viewmodel

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.jsoup.HttpStatusException
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.assets.AssetProvider
import os.dtakac.feritraspored.common.constants.SHOW_CHANGELOG
import os.dtakac.feritraspored.common.extensions.isSameWeek
import os.dtakac.feritraspored.common.extensions.scrollFormat
import os.dtakac.feritraspored.common.extensions.urlFormat
import os.dtakac.feritraspored.common.network.NetworkChecker
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.singlelivedata.SingleLiveEvent
import os.dtakac.feritraspored.schedule.data.ErrorData
import os.dtakac.feritraspored.schedule.data.JavascriptData
import os.dtakac.feritraspored.schedule.data.ScheduleData
import os.dtakac.feritraspored.schedule.repository.ScheduleRepository
import java.net.SocketTimeoutException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ScheduleViewModel(
        private val prefs: PreferenceRepository,
        private val scheduleRepository: ScheduleRepository,
        private val assetProvider: AssetProvider,
        private val networkChecker: NetworkChecker
) : ViewModel() {
    val scheduleData = MutableLiveData<ScheduleData>()
    val loaderVisible = MutableLiveData<Boolean>()
    val error = MutableLiveData<ErrorData?>()
    val controlsEnabled: LiveData<Boolean> = Transformations.map(loaderVisible) { !it }

    val javascript = SingleLiveEvent<JavascriptData>()
    val openInExternalBrowser = SingleLiveEvent<Uri>()
    val openInCustomTabs = SingleLiveEvent<Uri>()
    val showChangelog = SingleLiveEvent<Unit>()
    val snackBarMessage = SingleLiveEvent<@StringRes Int>()
    val webViewScroll = SingleLiveEvent<Float>()
    val clearWebViewScroll = SingleLiveEvent<Unit>()
    val bugReport = SingleLiveEvent<String>()

    private var wasLoadedInOnCreate = false
    private var selectedDate = buildCurrentDate()

    fun onViewCreated() {
        if (isOnline() && (scheduleData.value == null || prefs.isReloadToApplySettings)) {
            wasLoadedInOnCreate = true
            selectedDate = buildCurrentDate()
            loadSchedule()
        }
        if (SHOW_CHANGELOG && prefs.version < BuildConfig.VERSION_CODE) {
            showChangelog.call()
        }
    }

    fun onResume() {
        if (!wasLoadedInOnCreate && prefs.isLoadOnResume) {
            loadCurrentWeek()
        }
        wasLoadedInOnCreate = false
    }

    fun onPageDrawn() {
        loaderVisible.value = false
        if (buildCurrentDate().isSameWeek(selectedDate)) {
            scrollSelectedDateIntoView()
        }
    }

    fun onUrlClicked(url: String?) {
        try {
            openInCustomTabs.value = Uri.parse(url)
        } catch (e: Exception) {
            snackBarMessage.value = R.string.notify_error_open_link
        }
    }

    fun onRefreshClicked() {
        if (isOnline()) {
            loadSchedule()
        }
    }

    fun onOpenInExternalBrowserClicked() {
        try {
            openInExternalBrowser.value = Uri.parse(scheduleData.value?.baseUrl)
        } catch (e: Exception) {
            snackBarMessage.value = R.string.notify_error_open_external
        }
    }

    fun onPreviousWeekClicked() {
        if (isOnline()) {
            selectedDate = selectedDate.minusWeeks(1)
            loadSchedule()
        }
    }

    fun onCurrentWeekClicked() {
        loadCurrentWeek()
    }

    fun onNextWeekClicked() {
        if (isOnline()) {
            selectedDate = selectedDate.plusWeeks(1)
            loadSchedule()
        }
    }

    fun onBugReportClicked(displayedErrorMessage: String) {
        bugReport.value = buildBugReport(displayedErrorMessage)
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            clearWebViewScroll.call()
            error.value = null
            loaderVisible.value = true
            try {
                scheduleData.value = getScheduleData()
            } catch (e: Exception) {
                handleScheduleException(e)
            }
        }
    }

    private fun scrollSelectedDateIntoView() {
        viewModelScope.launch {
            val scrollJs = assetProvider
                    .readFile("template_scroll_into_view.js")
                    .format(selectedDate.scrollFormat())
            javascript.value = JavascriptData(
                    js = scrollJs,
                    callback = { postScrollEvent(it) }
            )
        }
    }

    private fun postScrollEvent(elementPosition: String) {
        val elementPositionDp = elementPosition.toFloatOrNull()
        if (elementPositionDp != null) {
            webViewScroll.value = elementPositionDp
        }
    }

    private fun buildCurrentDate(): LocalDate {
        var newSelectedDate = LocalDate.now()
        if (newSelectedDate.dayOfWeek == DayOfWeek.SATURDAY && prefs.isSkipSaturday) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        if (newSelectedDate.dayOfWeek == DayOfWeek.SUNDAY) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        if (prefs.isSkipDay && LocalTime.now() > prefs.time) {
            newSelectedDate = newSelectedDate.plusDays(1)
        }
        return newSelectedDate
    }

    private fun isOnline(): Boolean {
        val isOnline = networkChecker.isOnline
        if (!isOnline) {
            if (scheduleData.value == null) {
                if (error.value == null) {
                    error.value = ErrorData(R.string.error_no_network)
                } else {
                    snackBarMessage.value = R.string.notify_no_network
                }
            } else {
                snackBarMessage.value = R.string.notify_no_network
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
            },
            lightThemeCss = assetProvider.readFile("light_theme.css"),
            darkThemeCss = assetProvider.readFile("dark_theme.css")
    )

    private fun loadCurrentWeek() {
        val currentDate = buildCurrentDate()
        if (currentDate.isSameWeek(selectedDate) && scheduleData.value != null) {
            scrollSelectedDateIntoView()
        } else if (isOnline()) {
            selectedDate = currentDate
            loadSchedule()
        }
    }

    private fun handleScheduleException(e: Exception) {
        val errorMessage = when {
            e is HttpStatusException && e.statusCode in 400..599 -> R.string.error_page_unavailable
            e is SocketTimeoutException -> R.string.error_timeout
            else -> R.string.error_unexpected
        }
        error.value = ErrorData(
                message = errorMessage,
                exception = e
        )
        loaderVisible.value = false
    }

    private fun buildBugReport(displayedErrorMessage: String): String {
        val errorData = error.value
        return if (errorData == null) {
            ""
        } else {
            "[Displayed error message] $displayedErrorMessage" +
                    "\n---\n" +
                    "[Exception message] ${errorData.exception?.message}" +
                    "\n---\n" +
                    "[Identifier] ${prefs.courseIdentifier}" +
                    "\n---\n" +
                    "[Date] $selectedDate" +
                    "\n---\n" +
                    "[URL] ${scheduleData.value?.baseUrl}"
        }
    }
}