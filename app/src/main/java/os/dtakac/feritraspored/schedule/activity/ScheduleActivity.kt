package os.dtakac.feritraspored.schedule.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.event.observeEvent
import os.dtakac.feritraspored.common.utils.*
import os.dtakac.feritraspored.databinding.ActivityScheduleBinding
import os.dtakac.feritraspored.schedule.view_model.ScheduleViewModel
import os.dtakac.feritraspored.schedule.web_view_client.ScheduleWebViewClient
import os.dtakac.feritraspored.settings.container.SettingsActivity
import os.dtakac.feritraspored.views.debounce.onDebouncedClick

class ScheduleActivity: AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private val viewModel: ScheduleViewModel by viewModel()

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(
                binding.wvSchedule.url,
                resources.configuration.isNightMode()
        )
    }
    //endregion

    //region Initialization
    private fun initObservers() {
        viewModel.url.observeEvent(this) {
            binding.wvSchedule.loadUrl(it)
        }
        viewModel.title.observe(this) {
            binding.toolbar.title = it
        }
        viewModel.pageModificationJavascript.observeEvent(this) {
            injectPageModificationJavascript(it)
        }
        viewModel.weekNumberJavascript.observeEvent(this) {
            injectWeekNumberJavascript(it)
        }
        viewModel.openSettings.observeEvent(this) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        viewModel.openInExternalBrowser.observeEvent(this) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        }
        viewModel.openInCustomTabs.observeEvent(this) {
            CustomTabsIntent.Builder()
                    .setToolbarColor(resources.getColor(R.color.gray900))
                    .build()
                    .launchUrl(this, Uri.parse(it))
        }
        viewModel.showChangelog.observeEvent(this) {
            supportFragmentManager.showChangelog()
        }
        viewModel.snackBarMessage.observeEvent(this) {
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.openBugReport.observeEvent(this) {
            openBugReport(content = it)
        }
        viewModel.loaderVisibility.observeEvent(this) {
            binding.loader.root.visibility = it
        }
        viewModel.errorMessage.observeEvent(this) {
            binding.error.tvError.text = it
        }
        viewModel.errorVisibility.observeEvent(this) {
            binding.error.root.visibility = it
        }
        viewModel.controlsEnabled.observeEvent(this) {
            binding.navBar.apply {
                btnPrevious.isEnabled = it
                btnCurrent.isEnabled = it
                btnNext.isEnabled = it
            }
            binding.toolbar.menu.findItem(R.id.item_menu_refresh).isEnabled = it
        }
        viewModel.hideControls.observeEvent(this) {
            binding.toolbar.slide(Gravity.TOP, false)
        }
        viewModel.showControls.observe(this) {
            binding.toolbar.slide(Gravity.TOP, true)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.wvSchedule.apply {
            webViewClient = ScheduleWebViewClient(viewModel)
            settings.javaScriptEnabled = true
            scrollListener = viewModel.webViewScrollListener
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
    }

    private fun initBinding() {
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    //endregion

    //region WebView
    private fun injectPageModificationJavascript(script: String) {
        binding.wvSchedule.evaluateJavascript(script) {
            viewModel.onPageModificationJavascriptFinished()
        }
    }

    private fun injectWeekNumberJavascript(script: String) {
        binding.wvSchedule.evaluateJavascript(script) {
            viewModel.onWeekNumberJavascriptFinished(it)
        }
    }
    //endregion
}