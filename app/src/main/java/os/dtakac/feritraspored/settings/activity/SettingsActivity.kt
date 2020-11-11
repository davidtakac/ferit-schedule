package os.dtakac.feritraspored.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.databinding.ActivitySettingsBinding
import os.dtakac.feritraspored.settings.fragment.SettingsFragment
import os.dtakac.feritraspored.settings.fragment.SettingsFragmentOld

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        initBinding()
        initSettingsFragment()
        initToolbar()
    }

    private fun initBinding() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initSettingsFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment())
                .commit()
    }

    private fun initToolbar() {
        binding.toolbarSettings.setNavigationOnClickListener { finish() }
    }
}