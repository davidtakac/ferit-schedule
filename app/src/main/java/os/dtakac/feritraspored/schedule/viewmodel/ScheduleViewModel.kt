package os.dtakac.feritraspored.schedule.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.assets.AssetProvider
import os.dtakac.feritraspored.common.constants.SHOW_CHANGELOG
import os.dtakac.feritraspored.common.data.StringResourceWithArgs
import os.dtakac.feritraspored.common.extensions.isSameWeek
import os.dtakac.feritraspored.common.extensions.scrollFormat
import os.dtakac.feritraspored.common.extensions.urlFormat
import os.dtakac.feritraspored.common.network.NetworkChecker
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.singlelivedata.SingleLiveEvent
import os.dtakac.feritraspored.schedule.data.JavascriptData
import os.dtakac.feritraspored.schedule.data.ScheduleData
import os.dtakac.feritraspored.schedule.repository.ScheduleRepository
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
    val isLoaderVisible = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<StringResourceWithArgs?>()
    val isErrorGone: LiveData<Boolean> = Transformations.map(errorMessage) { it == null }
    val areControlsEnabled: LiveData<Boolean> = Transformations.map(isLoaderVisible) { !it }

    val javascript = SingleLiveEvent<JavascriptData>()
    val openInExternalBrowser = SingleLiveEvent<Uri>()
    val openInCustomTabs = SingleLiveEvent<Uri>()
    val showChangelog = SingleLiveEvent<Unit>()
    val snackBarMessage = SingleLiveEvent<Int>()
    val webViewScroll = SingleLiveEvent<Float>()
    val clearWebViewScroll = SingleLiveEvent<Unit>()

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
        isLoaderVisible.value = false
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

    fun scrollStudentGroupsIntoView() {
        viewModelScope.launch {
            val scrollJs = assetProvider
                .readFile("template_scroll_into_view.js")
                .format("tr.imegrupe")
            javascript.value = JavascriptData(
                js = scrollJs,
                callback = { postScrollEvent(it) }
            )
        }
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            clearWebViewScroll.call()
            errorMessage.value = null
            isLoaderVisible.value = true

            try {
                scheduleData.value = getScheduleData()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = StringResourceWithArgs(
                        content = R.string.template_error_unexpected,
                        args = listOf(
                                e.message ?: "",
                                prefs.courseIdentifier,
                                selectedDate.urlFormat()
                        )
                )
                isLoaderVisible.value = false
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
        elementPosition.toFloatOrNull()?.let {
            webViewScroll.value = it
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
                if (errorMessage.value == null) {
                    errorMessage.value = StringResourceWithArgs(R.string.error_no_network)
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
}