package os.dtakac.feritraspored.settings.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import os.dtakac.feritraspored.settings.view_model.PreferenceViewModel

val settingsModule = module {
    viewModel { PreferenceViewModel(get(), get()) }
}