package pt.up.fe.sdle.storage

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.logger
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.pathString

/**
 * Storage driver that persists its contents to the file system.
 */
class FileSystemStorageDriver : BaseStorageDriver() {
    override fun store(
        key: StorageKey,
        data: ShoppingList,
    ): Boolean {
        checkDataLocation()

        val filePath = Paths.get(DATA_FILE_LOCATION, key)

        if (filePath.exists() && !filePath.isRegularFile()) return false

        val dataFile = filePath.toFile()
        dataFile.setWritable(true)

        if (dataFile.exists() && !dataFile.canWrite()) return false

        val encodedData = Json.encodeToString(data)

        dataFile.writeText(encodedData)

        return true
    }

    override fun retrieve(key: StorageKey): ShoppingList? {
        checkDataLocation()

        val filePath = Paths.get(DATA_FILE_LOCATION, key)

        if (!filePath.exists() || !filePath.isRegularFile()) return null

        val dataFile = filePath.toFile()

        if (!dataFile.canRead()) return null

        // TODO: take into account that there is a 2GB limit for this
        val data = dataFile.readText()

        return Json.decodeFromString<ShoppingList>(data)
    }

    override fun keys(): List<StorageKey> {
        TODO("Not yet implemented")
    }

    override fun items(): List<ShoppingList> {
        TODO("Not yet implemented")
    }

    /**
     * Ensures that the data directory path exists.
     */
    private fun checkDataLocation() {
        val dataDirPath = Paths.get(DATA_FILE_LOCATION)

        if (!dataDirPath.exists()) {
            logger.trace("Creating directory $DATA_FILE_LOCATION")
            dataDirPath.createDirectories()
            logger.trace("Created directory at ${dataDirPath.pathString}: ${dataDirPath.isDirectory()}")
        }
    }

    companion object {
        private val DATA_FILE_LOCATION: String = Cluster.Config.Storage.FileSystem.DATA_FILE_LOCATION
    }
}
