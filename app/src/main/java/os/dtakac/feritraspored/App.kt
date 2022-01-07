package os.dtakac.feritraspored

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import os.dtakac.feritraspored.common.di.appModule
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.schedule.di.scheduleModule

@Suppress("unused")
class App : Application() {
    private val prefs: PreferenceRepository by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTheme()
    }

    private fun initKoin() {
        startKoin {
            // workaround for Duration crash: https://github.com/InsertKoinIO/koin/issues/1188#issuecomment-970240532
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(appModule, scheduleModule)
        }
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(prefs.theme)
    }
}