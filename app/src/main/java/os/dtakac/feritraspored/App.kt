package os.dtakac.feritraspored

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import os.dtakac.feritraspored.common.di.appModule
import os.dtakac.feritraspored.common.preferences.PreferenceRepository

class App: Application() {
    private val prefs: PreferenceRepository by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTheme()
        migrateToCourseIdentifierPreference()
    }

    private fun initKoin() {
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun initTheme() {
        val theme = prefs.theme?.toInt() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.also {
            prefs.theme = it.toString()
        }
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    private fun migrateToCourseIdentifierPreference() {
        if(prefs.courseIdentifier == null) {
            val year = prefs.year ?: return
            val programme = prefs.programme ?: return
            prefs.courseIdentifier = "${year}-${programme}"
            prefs.delete(R.string.key_year)
            prefs.delete(R.string.key_programme)
        }
    }
}