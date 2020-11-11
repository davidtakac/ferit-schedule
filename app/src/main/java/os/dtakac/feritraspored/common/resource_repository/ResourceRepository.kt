package os.dtakac.feritraspored.common.resource_repository

import androidx.annotation.StringRes

interface ResourceRepository {
    fun getString(@StringRes resId: Int): String
}