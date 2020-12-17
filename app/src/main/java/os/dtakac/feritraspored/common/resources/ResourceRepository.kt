package os.dtakac.feritraspored.common.resources

interface ResourceRepository {
    fun isOnline(): Boolean
    fun readFromAssets(fileName: String): String
}