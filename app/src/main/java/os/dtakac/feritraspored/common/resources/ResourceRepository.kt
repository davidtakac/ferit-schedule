package os.dtakac.feritraspored.common.resources

import androidx.annotation.StringRes

interface ResourceRepository {
    fun getString(@StringRes resId: Int): String
}