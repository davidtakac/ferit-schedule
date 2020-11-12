package os.dtakac.feritraspored.common.resources

import android.content.res.Configuration
import android.content.res.Resources

class ResourceRepositoryImpl(
        private val resources: Resources
): ResourceRepository {
    override fun getString(resId: Int): String {
        return resources.getString(resId)
    }

    override fun getStringArray(resId: Int): Array<String> {
        return resources.getStringArray(resId)
    }

    override fun getIntArray(resId: Int): Array<Int> {
        return resources.getIntArray(resId).toTypedArray()
    }

    override fun isNightMode(): Boolean {
        return when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }
}