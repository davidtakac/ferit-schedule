package os.dtakac.feritraspored.schedule.data

import kotlin.math.absoluteValue

data class ScrollData(
        val pixelsPerMillisecond: Int,
        val positionInPixels: Int
) {
    fun getScrollDuration(scrollPosition: Int): Long {
        return ((scrollPosition - positionInPixels).absoluteValue / pixelsPerMillisecond).toLong()
    }
}