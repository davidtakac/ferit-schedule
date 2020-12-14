package os.dtakac.feritraspored.schedule.data

import android.animation.TimeInterpolator
import kotlin.math.absoluteValue

data class ScrollData(
        private val speed: Double,
        val verticalPosition: Int,
        val interpolator: TimeInterpolator
) {
    fun getScrollDuration(currentVerticalPosition: Int): Long {
        val distance = (currentVerticalPosition - verticalPosition).absoluteValue
        return (distance / speed).toLong()
    }
}