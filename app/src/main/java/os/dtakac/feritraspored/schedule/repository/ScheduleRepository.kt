package os.dtakac.feritraspored.schedule.repository

import os.dtakac.feritraspored.schedule.data.ScheduleData

interface ScheduleRepository {
    suspend fun getScheduleData(
            scheduleUrl: String,
            showTimeOnBlocks: Boolean,
            filters: List<String>
    ): ScheduleData
}