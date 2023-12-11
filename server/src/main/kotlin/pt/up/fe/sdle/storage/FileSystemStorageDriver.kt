package pt.up.fe.sdle.storage

import pt.up.fe.sdle.crdt.ShoppingList
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

/**
 * Storage driver that persists its contents to the file system.
 */
class FileSystemStorageDriver : BaseStorageDriver() {

    override fun store(
        key: StorageKey,
        data: ShoppingList,
    ): Boolean {

        val filePath = Paths.get(DATA_FILE_LOCATION, key)

        if (!filePath.exists() || !filePath.isRegularFile()) return false

        val dataFile = filePath.toFile()



        return true
    }

    override fun retrieve(key: StorageKey): ShoppingList? {
        TODO("Not yet implemented")
    }

    companion object {
        const val DATA_FILE_LOCATION: String = ""
    }
}
