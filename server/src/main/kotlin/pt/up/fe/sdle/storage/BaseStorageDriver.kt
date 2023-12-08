package pt.up.fe.sdle.storage

/**
 *
 */
abstract class BaseStorageDriver: StorageDriver {

    override fun toString(): String {
        return BaseStorageDriver::class.simpleName ?: "BaseStorageDriver"
    }

}