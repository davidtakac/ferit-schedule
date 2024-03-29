package os.dtakac.feritraspored.common.constants

enum class ScheduleLanguage {
    HR, EN
}

fun getScheduleUrl(scheduleLanguage: ScheduleLanguage): String {
    return SCHEDULE_URLS[scheduleLanguage]!!
}

private val SCHEDULE_URLS = mapOf(
        ScheduleLanguage.HR to "https://www.ferit.unios.hr/studenti/raspored-nastave-i-ispita/%s/%s",
        ScheduleLanguage.EN to "https://www.ferit.unios.hr/students/schedule-of-classes-and-exams/%s/%s"
)