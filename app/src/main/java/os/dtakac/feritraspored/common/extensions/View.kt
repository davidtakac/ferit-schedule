package os.dtakac.feritraspored.common.extensions

import android.view.View

var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if(value) View.GONE else View.VISIBLE
    }