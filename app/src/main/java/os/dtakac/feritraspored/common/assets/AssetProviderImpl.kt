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
            val scanner = Scanner(applicationContext.assets.open(fileName))
            val stringBuilder = StringBuilder()
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine())
            }
            stringBuilder.toString()
        }
    }
}