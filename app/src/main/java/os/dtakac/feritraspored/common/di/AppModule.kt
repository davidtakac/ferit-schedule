package os.dtakac.feritraspored.common.di

import android.content.Context
import androidx.preference.PreferenceManager
import org.koin.dsl.module
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.preferences.PreferenceRepositoryImpl
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepositoryImpl
import os.dtakac.feritraspored.common.scripts.ScriptProvider
import os.dtakac.feritraspored.common.scripts.ScriptProviderImpl

val appModule = module {
    single<ResourceRepository>{ ResourceRepositoryImpl(get<Context>().resources) }
    single<PreferenceRepository>{ PreferenceRepositoryImpl(get(), PreferenceManager.getDefaultSharedPreferences(get())) }
    single<ScriptProvider>{ ScriptProviderImpl(get<Context>().assets, get()) }
}