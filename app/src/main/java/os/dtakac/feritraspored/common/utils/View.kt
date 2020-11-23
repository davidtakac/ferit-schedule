package os.dtakac.feritraspored.common.utils

import android.view.View
import android.view.ViewGroup
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.Visibility

fun View.slide(edge: Int, show: Boolean) {
    this.visibility = if(show) View.VISIBLE else View.GONE
    /*val transition = Slide(edge)
    transition.addTarget(this)
    transition.duration = 300
    TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)*/
}