package os.dtakac.feritraspored.common.assets

interface AssetProvider {
    suspend fun readFile(fileName: String): String
}