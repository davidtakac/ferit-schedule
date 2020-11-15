package os.dtakac.feritraspored.schedule.view_model

import android.view.View
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import os.dtakac.feritraspored.BuildConfig
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.event.Event
import os.dtakac.feritraspored.common.event.peekContent
import os.dtakac.feritraspored.common.event.postEvent
import os.dtakac.feritraspored.common.network.NetworkUtil
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.scripts.ScriptProvider
import os.dtakac.feritraspored.common.utils.isSameWeek
import os.dtakac.feritraspored.schedule.web_view_client.ScheduleWebViewClient
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository,
        private val scriptProvider: ScriptProvider,
        private val networkUtil: NetworkUtil
): ViewModel(), ScheduleWebViewClient.Listener {
    val url = MutableLiveData<Event<String>>()
    val title = MutableLiveData(res.getString(R.string.label_schedule))
    val pageModificationJavascript = MutableLiveData<Event<String>>()
    val weekNumberJavascript = MutableLiveData<Event<String>>()
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

    private var selectedDate = LocalDate.now()
        set(value) {
            field = value
            buildAndPostUrl()
        }
    private var isNightMode: Boolean = false

    //region Lifecycle
    fun onResume(currentNightMode: Boolean) {
        if(prefs.version < BuildConfig.VERSION_CODE) {
            showChangelog.postEvent()
        }
        if( isNightMode != currentNightMode ||
            prefs.isSettingsModified ||
            prefs.isLoadOnResume ||
            url.value == null
        ) {
            isNightMode = currentNightMode
            onCurrentWeekClicked()
        }
    }
    //endregion

    //region WebView
    override fun onOverrideUrlLoading(url: String) {
        openInCustomTabs.postEvent(url)
    }

    override fun onPageStarted() { /*loaders are handled in [buildAndPostUrl]*/ }

    override fun onErrorReceived(code: Int, description: String?, url: String?) {
        val errorMessage = if(!networkUtil.isOnline()) {
            res.getString(R.string.notify_no_network)
        } else {
            res.getString(R.string.template_error_unexpected).format(
                code, description, url
            )
        }
        this.errorMessage.postEvent(errorMessage)
        loaderVisibility.postEvent(View.GONE)
    }

    override fun onPageFinished(isError: Boolean) {
        if(!isError) {
            weekNumberJavascript.postEvent(scriptProvider.weekNumberFunction())
            pageModificationJavascript.postEvent(buildPageModificationJavascript())
        }
    }

    fun onPageModificationJavascriptFinished() {
        viewModelScope.launch {
            delay(250) //gives javascript time to apply itself
            loaderVisibility.postEvent(View.GONE)
        }
    }

    fun onWeekNumberJavascriptFinished(returnedWeekNumber: String) {
        title.postValue(
            if( returnedWeekNumber.isEmpty() ||
                returnedWeekNumber.isBlank() ||
                returnedWeekNumber == "null" ||
                returnedWeekNumber == "undefined"
            ) {
                res.getString(R.string.label_schedule)
            } else {
                returnedWeekNumber.removeSurrounding("\"")
            }
        )
    }
    //endregion

    //region Click handling
    fun onRefreshClicked() {
        buildAndPostUrl()
    }

    fun onSettingsClicked() {
        openSettings.postValue(Event(Unit))
    }

    fun onOpenInExternalBrowserClicked() {
        val urlToOpen = url.peekContent() ?: return
        openInExternalBrowser.postEvent(urlToOpen)
    }

    fun onPreviousWeekClicked() {
        selectedDate = selectedDate.minusWeeks(1)
    }

    fun onCurrentWeekClicked() {
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
        selectedDate = newSelectedDate
    }

    fun onNextWeekClicked() {
        selectedDate = selectedDate.plusWeeks(1)
    }

    fun onBugReportClicked() {
        openBugReport.postEvent(
                res.getString(R.string.template_bug_report).format(
                        errorMessage.peekContent() ?: ""
                )
        )
    }
    //endregion

    private fun buildAndPostUrl() {
        if(networkUtil.isOnline()) {
            errorMessage.postEvent(null)
            loaderVisibility.postEvent(View.VISIBLE)
            url.postEvent(buildUrl())
        } else {
            snackBarMessage.postEvent(res.getString(R.string.notify_no_network))
        }
    }

    private fun buildUrl(): String {
        return res.getString(R.string.template_schedule).format(
                selectedDate.toString(),
                prefs.courseIdentifier
        )
    }

    private fun buildPageModificationJavascript(): String {
        var js = scriptProvider.hideJunkFunction() + scriptProvider.timeOnBlocksFunction()
        if(isNightMode) {
            js += scriptProvider.darkThemeFunction()
        }
        if(prefs.isFiltersEnabled) {
            val filtersTrimmed = prefs.filters?.split(",")?.map { it.trim() } ?: listOf()
            js += scriptProvider.highlightBlocksFunction(filtersTrimmed)
        }
        if(selectedDate.isSameWeek(LocalDate.now())) {
            js += scriptProvider.scrollIntoViewFunction(
                    selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
            )
        }
        return js
    }
}