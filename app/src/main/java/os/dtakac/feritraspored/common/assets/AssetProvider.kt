package os.dtakac.feritraspored.common.assets

interface AssetProvider {
    fun readFile(fileName: String): String
}