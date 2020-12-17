package os.dtakac.feritraspored.settings.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import os.dtakac.feritraspored.settings.viewmodel.PreferenceViewModel

val settingsModule = module {
    viewModel { PreferenceViewModel(get()) }
}