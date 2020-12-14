package os.dtakac.feritraspored.common.resources

import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.StringRes

interface ResourceRepository {
    fun getString(@StringRes resId: Int): String
    fun getStringArray(@ArrayRes resId: Int): Array<String>
    fun getBoolean(@BoolRes resId: Int): Boolean
    fun isOnline(): Boolean
    fun readFromAssets(fileName: String): String
    fun toPx(dp: Float): Float
}