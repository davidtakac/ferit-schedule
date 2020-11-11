package os.dtakac.feritraspored.common.utils

import androidx.annotation.ArrayRes
import androidx.fragment.app.Fragment

fun Fragment.getStringArray(@ArrayRes arrayResId: Int): Array<String> {
    return resources.getStringArray(arrayResId)
}