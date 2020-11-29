package os.dtakac.feritraspored.schedule.repository

import os.dtakac.feritraspored.schedule.data.ScheduleData
import java.time.LocalDate

interface ScheduleRepository {
    suspend fun getScheduleData(withDate: LocalDate): ScheduleData
}