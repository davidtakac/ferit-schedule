package os.dtakac.feritraspored.schedule.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.event.observeEvent
import os.dtakac.feritraspored.common.extensions.getColorCompat
import os.dtakac.feritraspored.common.extensions.isNightMode
import os.dtakac.feritraspored.common.extensions.openEmailEditor
import os.dtakac.feritraspored.common.extensions.showChangelog
import os.dtakac.feritraspored.databinding.ActivityScheduleBinding
import os.dtakac.feritraspored.schedule.data.ScrollData
import os.dtakac.feritraspored.schedule.view_model.ScheduleViewModel
import os.dtakac.feritraspored.settings.container.SettingsActivity
import os.dtakac.feritraspored.views.debounce.onDebouncedClick

class ScheduleActivity: AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private val viewModel: ScheduleViewModel by viewModel()
    private var scrollAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        initLifecycleObserver()
        initBinding()
        initViews()
        initObservers()
        viewModel.onCreate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initObservers() {
        viewModel.scheduleData.observe(this) {
            binding.wvSchedule.loadDataWithBaseURL(
                    it.baseUrl,
                    if(resources.configuration.isNightMode()) it.htmlDark else it.html,
                    it.mimeType,
                    it.encoding,
                    null
            )
            binding.toolbar.title = it.title
        }
        viewModel.javascript.observeEvent(this) { data ->
            binding.wvSchedule.evaluateJavascript(data.js) {
                data.callback.invoke(it)
            }
        }
        viewModel.webViewScroll.observeEvent(this) {
            scrollWebView(it)
        }
        viewModel.clearWebViewScroll.observeEvent(this) {
            scrollAnimator?.cancel()
        }
        viewModel.openSettings.observeEvent(this) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        viewModel.openInExternalBrowser.observeEvent(this) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        }
        viewModel.openInCustomTabs.observeEvent(this) {
            val colorParams = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(getColorCompat(R.color.colorStatusBar))
                    .build()
            CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(colorParams)
                    .build()
                    .launchUrl(this, Uri.parse(it))
        }
        viewModel.showChangelog.observeEvent(this) {
            supportFragmentManager.showChangelog()
        }
        viewModel.snackBarMessage.observeEvent(this) {
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.navBar.root)
                    .show()
        }
        viewModel.openEmailEditor.observeEvent(this) {
            openEmailEditor(it)
        }
        viewModel.isLoaderVisible.observe(this) { shouldShow ->
            binding.loader.apply { if(shouldShow) show() else hide() }
        }
        viewModel.errorMessage.observe(this) {
            binding.error.tvError.text = it
        }
        viewModel.isErrorGone.observe(this) {
            binding.error.root.isGone = it
        }
        viewModel.areControlsEnabled.observe(this) {
            binding.navBar.apply {
                btnPrevious.isEnabled = it
                btnCurrent.isEnabled = it
                btnNext.isEnabled = it
            }
            binding.toolbar.menu.findItem(R.id.item_menu_refresh).isEnabled = it
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.wvSchedule.apply {
            setBackgroundColor(getColorCompat(android.R.color.transparent))
            webViewClient = scheduleWebViewClient
            settings.javaScriptEnabled = true
        }
        binding.toolbar.apply {
            inflateMenu(R.menu.menu)
            menu.findItem(R.id.item_menu_refresh).onDebouncedClick {
                viewModel.onRefreshClicked()
            }
            menu.findItem(R.id.item_menu_settings).onDebouncedClick {
                viewModel.onSettingsClicked()
            }
            menu.findItem(R.id.item_menu_browser).onDebouncedClick {
                viewModel.onOpenInExternalBrowserClicked()
            }
        }
        binding.navBar.apply {
            btnPrevious.onDebouncedClick {
                viewModel.onPreviousWeekClicked()
            }
            btnCurrent.onDebouncedClick {
                viewModel.onCurrentWeekClicked()
            }
            btnNext.onDebouncedClick {
                viewModel.onNextWeekClicked()
            }
        }
        binding.error.btnBugReport.setOnClickListener {
            viewModel.onBugReportClicked()
        }
        binding.loader.hide()
    }

    private fun initBinding() {
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initLifecycleObserver() {
        lifecycle.addObserver(pageDrawnLifecycleObserver)
    }

    private fun scrollWebView(data: ScrollData) {
        if(scrollAnimator?.isStarted != true) {
            val currentVerticalPosition = binding.wvSchedule.scrollY
            val anim = ObjectAnimator.ofInt(
                    binding.wvSchedule,
                    "scrollY",
                    currentVerticalPosition,
                    data.verticalPosition
            )
            anim.duration = data.getScrollDuration(currentVerticalPosition)
            anim.interpolator = data.interpolator
            scrollAnimator = anim
            scrollAnimator?.start()
        }
    }

    private val scheduleWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            pageDrawnLifecycleObserver.postPageDrawn()
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            viewModel.onUrlClicked(url)
            return true
        }
    }

    /**
     * Dispatches [ScheduleViewModel.onPageDrawn] only when the [androidx.lifecycle.LifecycleOwner]
     * is [Lifecycle.State.RESUMED]. This makes sure that it doesn't get called when
     * the page isn't drawn yet, which causes the scroll distance to be measured incorrectly.
     */
    private val pageDrawnLifecycleObserver = object : LifecycleObserver {
        var wasPageDrawn = false

        fun postPageDrawn() {
            wasPageDrawn = true
            if(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                dispatchPageDrawn()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        private fun dispatchPageDrawn() {
            if(wasPageDrawn) {
                viewModel.onPageDrawn()
            }
            wasPageDrawn = false
        }
    }
}