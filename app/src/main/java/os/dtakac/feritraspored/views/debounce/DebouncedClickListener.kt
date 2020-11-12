package os.dtakac.feritraspored.views.debounce

import android.os.SystemClock
import android.view.View

class DebouncedClickListener(
        private val threshold: Long,
        private val listener: () -> Unit
): View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(view: View?) {
        if(SystemClock.elapsedRealtime() - lastClickTime < threshold) {
            //consume click and prevent others from executing
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        listener.invoke()
    }
}

fun View.onDebouncedClick(debounceInterval: Long, listener: () -> Unit) {
    setOnClickListener(DebouncedClickListener(debounceInterval, listener))
}