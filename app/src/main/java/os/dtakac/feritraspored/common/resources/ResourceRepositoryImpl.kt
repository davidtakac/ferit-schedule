package os.dtakac.feritraspored.common.resources

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

class ResourceRepositoryImpl(
        private val context: Context
): ResourceRepository {
    override fun getString(resId: Int): String {
        return context.resources.getString(resId)
    }

    override fun getStringArray(resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }

    override fun getColorHex(resId: Int): String {
        return "#${Integer.toHexString(ContextCompat.getColor(context, resId) and 0x00ffffff)}"
    }

    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager
        //yes, its deprecated, but for our use case its good enough
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}