package pt.up.fe.sdle.storage

/**
 * Storage driver that persists its contents to the file system.
 */
class FileSystemStorageDriver<T> : BaseStorageDriver<T>() {
    override fun store(
        key: StorageKey,
        data: T,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun retrieve(key: StorageKey): T? {
        TODO("Not yet implemented")
    }
}
