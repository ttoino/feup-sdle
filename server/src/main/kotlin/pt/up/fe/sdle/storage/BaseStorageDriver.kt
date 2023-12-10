package pt.up.fe.sdle.storage

/**
 *
 */
sealed class BaseStorageDriver<T> : StorageDriver<T> {
    override fun toString(): String {
        return BaseStorageDriver::class.simpleName ?: "BaseStorageDriver"
    }
}
