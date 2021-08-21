package os.dtakac.feritraspored.schedule.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import os.dtakac.feritraspored.schedule.repository.ScheduleRepository
import os.dtakac.feritraspored.schedule.repository.ScheduleRepositoryImpl
import os.dtakac.feritraspored.schedule.viewmodel.ScheduleViewModel

val scheduleModule = module {
    factory<ScheduleRepository> { ScheduleRepositoryImpl() }
    viewModel { ScheduleViewModel(get(), get(), get(), get()) }
}