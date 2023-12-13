package pt.up.fe.sdle.storage

import pt.up.fe.sdle.crdt.ShoppingList

/**
 * Storage driver that keeps all data in memory.
 *
 * This driver, while keeping data ephemeral, allows for faster access to data.
 */
class MemoryStorageDriver : BaseStorageDriver() {
    private val items: MutableMap<StorageKey, ShoppingList> = mutableMapOf()

    override fun store(
        key: StorageKey,
        data: ShoppingList,
    ): Boolean {
        items[key] = data

        return true
    }

    override fun retrieve(key: StorageKey): ShoppingList? = items[key]

    override fun keys(): List<StorageKey> = items.keys.toList()

    override fun items(): List<ShoppingList> = items.values.toList()
}
