package os.dtakac.feritraspored.common.extensions

import android.content.res.Resources

val Int.px
    get() = Resources.getSystem().displayMetrics.density.toInt() * this