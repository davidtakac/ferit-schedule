package os.dtakac.feritraspored.common.extensions

import android.util.DisplayMetrics
import android.util.TypedValue

fun DisplayMetrics.toPixels(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this)