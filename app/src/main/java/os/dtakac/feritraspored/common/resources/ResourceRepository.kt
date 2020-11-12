package os.dtakac.feritraspored.common.resources

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

interface ResourceRepository {
    fun getString(@StringRes resId: Int): String
    fun getStringArray(@ArrayRes resId: Int): Array<String>
}