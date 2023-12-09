package pt.up.fe.sdle.storage

/**
 * Storage driver that keeps all data in memory.
 *
 * This driver, while keeping data ephemeral, allows for faster access to data.
 */
class MemoryStorageDriver<T> : BaseStorageDriver<T>() {
    private val items: MutableMap<StorageKey, T> = mutableMapOf()

    override fun store(
        key: StorageKey,
        data: T,
    ): Boolean {
        items[key] = data

        return true
    }

    override fun retrieve(key: StorageKey): T? = items[key]
}
