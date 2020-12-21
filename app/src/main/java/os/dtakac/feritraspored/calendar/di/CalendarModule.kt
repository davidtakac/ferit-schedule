package os.dtakac.feritraspored.calendar.di

import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import os.dtakac.feritraspored.calendar.repository.CalendarRepository
import os.dtakac.feritraspored.calendar.repository.CalendarRepositoryImpl
import os.dtakac.feritraspored.calendar.viewmodel.CalendarViewModel

val calendarModule = module {
    factory<CalendarRepository> { CalendarRepositoryImpl(androidContext().contentResolver) }
    viewModel { CalendarViewModel(get()) }
}