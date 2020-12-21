package os.dtakac.feritraspored.calendar.adapter.calendars

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import os.dtakac.feritraspored.common.extensions.toPixels

class CalendarItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val horizontalMargin  = view.resources.displayMetrics.toPixels(8f).toInt()
        outRect.left = horizontalMargin
        outRect.right = horizontalMargin
    }
}