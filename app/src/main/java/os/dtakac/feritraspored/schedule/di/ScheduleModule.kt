package os.dtakac.feritraspored.schedule.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import os.dtakac.feritraspored.schedule.view_model.ScheduleViewModel

val scheduleModule = module {
    viewModel { ScheduleViewModel(get(), get(), get(), get()) }
}