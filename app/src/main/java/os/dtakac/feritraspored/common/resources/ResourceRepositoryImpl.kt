package os.dtakac.feritraspored.common.resources

import android.content.Context
import android.net.ConnectivityManager
import java.util.*

class ResourceRepositoryImpl(
        private val context: Context
): ResourceRepository {
    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager
        @Suppress("DEPRECATION")
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    override fun readFromAssets(fileName: String): String {
        val scanner = Scanner(context.assets.open(fileName))
        val stringBuilder = StringBuilder()
        while(scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine())
        }
        return stringBuilder.toString()
    }
}