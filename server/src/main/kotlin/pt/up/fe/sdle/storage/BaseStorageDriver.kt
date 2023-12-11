package pt.up.fe.sdle.storage

/**
 * Base storage driver implementation that implements some functionality for [StorageDriver]
 */
sealed class BaseStorageDriver : StorageDriver {
    override fun toString(): String {
        return BaseStorageDriver::class.simpleName ?: "BaseStorageDriver"
    }
}
