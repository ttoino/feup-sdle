package pt.up.fe.sdle.storage

/**
 * Storage driver that keeps all data in memory.
 *
 * This driver, while keeping data ephemeral, allows for faster access to data.
 */
class MemoryStorageDriver : BaseStorageDriver() {
    override fun store(
        data: Any,
        key: StorageKey?,
    ) {
        TODO("Not yet implemented")
    }
}
