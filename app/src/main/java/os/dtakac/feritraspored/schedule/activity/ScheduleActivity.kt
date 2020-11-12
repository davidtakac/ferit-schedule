package os.dtakac.feritraspored.schedule.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.event.observeEvent
import os.dtakac.feritraspored.common.utils.openInExternalBrowserIntent
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
        viewModel.onResume(resources.configuration)
    }
    //endregion

    //region Initialization
    private fun initObservers() {
        viewModel.url.observeEvent(this) {
            binding.wvSchedule.loadUrl(it)
        }
        viewModel.javascript.observeEvent(this) {
            injectJavascript(it)
        }
        viewModel.openSettings.observeEvent(this) {
            goToSettings()
        }
        viewModel.openInExternalBrowser.observeEvent(this) {
            openInExternalBrowser(it)
        }
        viewModel.loaderVisibility.observeEvent(this) {
            binding.loader.root.visibility = it
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.wvSchedule.apply {
            webViewClient = ScheduleWebViewClient(viewModel)
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
    }

    private fun initBinding() {
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    //endregion

    //region WebView
    private fun injectJavascript(script: String) {
        binding.wvSchedule.evaluateJavascript(script) {
            viewModel.onJavascriptFinished()
        }
    }
    //endregion

    //region Navigation
    private fun goToSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openInExternalBrowser(url: String) {
        startActivity(openInExternalBrowserIntent(url))
    }
    //endregion
}