package os.dtakac.feritraspored.schedule.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.databinding.ActivityScheduleBinding
import os.dtakac.feritraspored.schedule.view_model.ScheduleViewModel

class ScheduleActivity: AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private val viewModel: ScheduleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initObservers() {
        viewModel.url.observe(this) {
            binding.wvSchedule.loadUrl(it)
        }
        viewModel.javascript.observe(this) {
            binding.wvSchedule.evaluateJavascript(it) { viewModel.onJavascriptFinished() }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.wvSchedule.apply {
            webViewClient = WebViewClient() //todo: custom
            settings.javaScriptEnabled = true
        }
    }

    private fun initBinding() {
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}