package os.dtakac.feritraspored.common.network

import android.content.Context
import android.net.ConnectivityManager

class NetworkCheckerImpl(
        private val applicationContext: Context
) : NetworkChecker {
    override val isOnline: Boolean
        get() {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as? ConnectivityManager
            @Suppress("DEPRECATION")
            return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
}