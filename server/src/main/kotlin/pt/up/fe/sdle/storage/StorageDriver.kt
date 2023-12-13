package pt.up.fe.sdle.storage

import pt.up.fe.sdle.crdt.ShoppingList

/**
 * The type of keys used to store data.
 */
typealias StorageKey = String

/**
 * A [StorageDriver] is responsible for storing data of type [ShoppingList] supplied to it.
 *
 * Implementations are responsible for defining the mechanisms by which that action is made.
 *
 * Examples of implementations include [FileSystemStorageDriver] and [MemoryStorageDriver]
 */
sealed interface StorageDriver {
    /**
     * Stores the given piece of data, optionally using the supplied [StorageKey].
     *
     * @param [key] the key under which to store data
     * @param [data] the data to store
     * @return whether this operation succeeded
     */
    fun store(
        key: StorageKey,
        data: ShoppingList,
    ): Boolean

    /**
     * Retrieves the data of type [ShoppingList] stored under the given [StorageKey].
     *
     * @param [key] the key under which the desired data is stored,
     * @return the data item, or null if not found.
     */
    fun retrieve(key: StorageKey): ShoppingList?

    /**
     * Returns all stored keys.
     *
     * @return A list of all stored keys
     */
    fun keys(): List<StorageKey>

    /**
     * Returns all stored data items.
     *
     * @return A list of all stored items.
     */
    fun items(): List<ShoppingList>
}
