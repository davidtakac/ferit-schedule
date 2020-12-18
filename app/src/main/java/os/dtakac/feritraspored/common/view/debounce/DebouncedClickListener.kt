package os.dtakac.feritraspored.common.view.debounce

import android.os.SystemClock
import android.view.View
import os.dtakac.feritraspored.common.constants.DEBOUNCE_INTERVAL_MS

class DebouncedClickListener(
        private val threshold: Long,
        private val listener: () -> Unit
) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(view: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < threshold) {
            //consume click and prevent others from executing
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        listener.invoke()
    }
}

fun View.onDebouncedClick(debounceInterval: Long = DEBOUNCE_INTERVAL_MS, listener: () -> Unit) {
    setOnClickListener(DebouncedClickListener(debounceInterval, listener))
}