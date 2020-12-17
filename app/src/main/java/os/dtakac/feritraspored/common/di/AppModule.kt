package os.dtakac.feritraspored.common.di

import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.preferences.PreferenceRepositoryImpl
import os.dtakac.feritraspored.common.resources.ResourceRepository
import os.dtakac.feritraspored.common.resources.ResourceRepositoryImpl

val appModule = module {
    single<ResourceRepository>{ ResourceRepositoryImpl(androidContext()) }
    single<PreferenceRepository> {
        PreferenceRepositoryImpl(PreferenceManager.getDefaultSharedPreferences(get()))
    }
}