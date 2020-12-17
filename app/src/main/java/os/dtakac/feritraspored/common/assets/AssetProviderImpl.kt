package os.dtakac.feritraspored.common.assets

import android.content.Context
import java.util.*

class AssetProviderImpl(
        private val applicationContext: Context
) : AssetProvider {
    override fun readFile(fileName: String): String {
        val scanner = Scanner(applicationContext.assets.open(fileName))
        val stringBuilder = StringBuilder()
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine())
        }
        return stringBuilder.toString()
    }
}