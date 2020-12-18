package os.dtakac.feritraspored.common.assets

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AssetProviderImpl(
        private val applicationContext: Context
) : AssetProvider {
    override suspend fun readFile(fileName: String): String {
        return withContext(Dispatchers.IO) {
            val stringBuilder = StringBuilder()

            @Suppress("BlockingMethodInNonBlockingContext")
            applicationContext.assets.open(fileName).use {
                val scanner = Scanner(it)
                while (scanner.hasNextLine()) {
                    stringBuilder.append(scanner.nextLine())
                }
                stringBuilder.toString()
            }
        }
    }
}