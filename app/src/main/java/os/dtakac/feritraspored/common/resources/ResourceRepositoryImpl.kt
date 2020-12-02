package os.dtakac.feritraspored.common.resources

import android.content.Context
import android.net.ConnectivityManager
import android.util.TypedValue
import os.dtakac.feritraspored.common.extensions.getColorCompat
import java.util.*

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
        return "#${Integer.toHexString(context.getColorCompat(resId) and 0x00ffffff)}"
    }

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

    override fun toPx(dp: Float): Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
        )
    }
}