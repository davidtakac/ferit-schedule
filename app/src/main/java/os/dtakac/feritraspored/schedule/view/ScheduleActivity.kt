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
        initBinding()
        initViews()
        initObservers()
        viewModel.onCreate(resources.configuration.isNightMode())
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initObservers() {
        viewModel.scheduleData.observeEvent(this) {
            binding.wvSchedule.loadDataWithBaseURL(
                    it.baseUrl,
                    it.html,
                    it.mimeType,
                    it.encoding,
                    null
            )
            binding.toolbar.title = it.title
        }
        viewModel.title.observe(this) {
            binding.toolbar.title = it
        }
        viewModel.javascript.observeEvent(this) { data ->
            binding.wvSchedule.evaluateJavascript(data.js) {
                data.callback.invoke(it)
            }
        }
        viewModel.webViewScroll.observeEvent(this) {
            scrollWebView(it)
        }
        viewModel.cancelWebViewScroll.observeEvent(this) {
            scrollAnimator?.cancel()
            scrollAnimator = null
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
        viewModel.isLoaderVisible.observeEvent(this) { shouldShow ->
            binding.loader.apply { if(shouldShow) show() else hide() }
        }
        viewModel.errorMessage.observeEvent(this) {
            binding.error.tvError.text = it
        }
        viewModel.isErrorGone.observeEvent(this) {
            binding.error.root.isGone = it
        }
        viewModel.areControlsEnabled.observeEvent(this) {
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

    private fun scrollWebView(data: ScrollData) {
        if(scrollAnimator?.isRunning == true) return

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

    private val scheduleWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            viewModel.onPageFinished()
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            viewModel.onUrlClicked(url)
            return true
        }
    }
}