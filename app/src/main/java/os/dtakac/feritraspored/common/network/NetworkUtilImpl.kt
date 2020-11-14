package os.dtakac.feritraspored.common.network

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtilImpl(
        private val context: Context
): NetworkUtil {
    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager
        //yes, its deprecated, but for our use case its good enough
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}