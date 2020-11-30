package os.dtakac.feritraspored.common.resources

import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface ResourceRepository {
    fun getString(@StringRes resId: Int): String
    fun getStringArray(@ArrayRes resId: Int): Array<String>
    fun getColorHex(@ColorRes resId: Int): String
    fun isOnline(): Boolean
    fun readFromAssets(fileName: String): String
}