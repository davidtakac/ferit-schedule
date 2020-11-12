package os.dtakac.feritraspored.schedule.view_model

import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.event.Event
import os.dtakac.feritraspored.common.event.postEvent
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.scripts.ScriptProvider
import os.dtakac.feritraspored.common.utils.isNightMode
import os.dtakac.feritraspored.common.utils.isSameWeek
import os.dtakac.feritraspored.schedule.web_view_client.ScheduleWebViewClient
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleViewModel(
        private val prefs: PreferenceRepository,
        private val res: ResourceRepository,
        private val scriptProvider: ScriptProvider
): ViewModel(), ScheduleWebViewClient.Listener {
    val url = MutableLiveData<Event<String>>()
    val javascript = MutableLiveData<Event<String>>()
    val loaderVisibility = MutableLiveData<Event<Int>>()
    val openSettings = MutableLiveData<Event<Unit>>()
    val openInExternalBrowser = MutableLiveData<Event<String>>()
    val openInCustomTabs = MutableLiveData<Event<String>>()

    private var selectedDate: LocalDate = nowDate
    private val nowDate: LocalDate
        get() = LocalDate.now()
    private val nowTime: LocalTime
        get() = LocalTime.now()

    private var isNightMode: Boolean = false

    //region Lifecycle
    fun onResume(configuration: Configuration) {
        if(prefs.isSettingsModified || prefs.isLoadOnResume) {
            url.postEvent(buildUrl())
        }
        isNightMode = configuration.isNightMode()
    }
    //endregion

    //region ScheduleWebViewClient.Listener
    override fun onOverrideUrlLoading(request: WebResourceRequest?) {
        val urlToOpen = request?.url?.toString() ?: return
        openInCustomTabs.postEvent(urlToOpen)
    }

    override fun onPageStarted() {
        loaderVisibility.postEvent(View.VISIBLE)
    }

    override fun onErrorReceived(request: WebResourceRequest?, error: WebResourceError?) {
        //todo: implement showing error message and hide the loader
    }

    override fun onPageFinished(isError: Boolean) {
        if(!isError) {
            javascript.postEvent(buildJavascript())
        }
    }
    //endregion

    fun onJavascriptFinished() {
        viewModelScope.launch {
            delay(200) //gives javascript time to apply itself
            loaderVisibility.postEvent(View.GONE)
        }
    }
    //endregion

    //region Click handling
    fun onRefreshClicked() {
        url.postEvent(buildUrl())
    }

    fun onSettingsClicked() {
        openSettings.postValue(Event(Unit))
    }

    fun onOpenInExternalBrowserClicked() {
        val urlToOpen = url.value?.peekContent() ?: return
        openInExternalBrowser.postEvent(urlToOpen)
    }
    //endregion

    private fun buildUrl(): String {
        return res.getString(R.string.template_schedule).format(
                selectedDate.toString(),
                prefs.courseIdentifier
        )
    }

    private fun buildJavascript(): String {
        var js = scriptProvider.hideJunkFunction() + scriptProvider.timeOnBlocksFunction()
        if(isNightMode) {
            js += scriptProvider.darkThemeFunction()
        }
        if(prefs.isFiltersEnabled) {
            val filtersTrimmed = prefs.filters?.split(",")?.map { it.trim() } ?: listOf()
            js += scriptProvider.highlightBlocksFunction(filtersTrimmed)
        }
        if(selectedDate.isSameWeek(nowDate)) {
            val anchor = if(
                    prefs.isSkipDay &&
                    nowTime.hour >= prefs.timeHour &&
                    nowTime.minute >= prefs.timeMinute
            ) {
                nowDate.plusDays(1)
            } else {
                nowDate
            }
            js += scriptProvider.scrollIntoViewFunction(
                    anchor.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
            )
        }
        return js
    }
}