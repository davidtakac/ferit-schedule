package os.dtakac.feritraspored.settings.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import os.dtakac.feritraspored.settings.view_model.SettingsViewModel

val settingsModule = module {
    viewModel { SettingsViewModel(get(), get()) }
}