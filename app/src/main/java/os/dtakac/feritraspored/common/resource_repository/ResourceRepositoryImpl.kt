package os.dtakac.feritraspored.common.resource_repository

import android.content.res.Resources

class ResourceRepositoryImpl(
    private val resources: Resources
): ResourceRepository {
    override fun getString(resId: Int): String {
        return resources.getString(resId)
    }
}