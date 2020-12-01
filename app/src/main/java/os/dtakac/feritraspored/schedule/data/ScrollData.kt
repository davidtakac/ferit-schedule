package os.dtakac.feritraspored.schedule.data

import kotlin.math.absoluteValue

data class ScrollData(
        private val speed: Double,
        val verticalPosition: Int
) {
    fun getScrollDuration(currentVerticalPosition: Int): Long {
        return ((currentVerticalPosition - verticalPosition).absoluteValue / speed).toLong()
    }
}