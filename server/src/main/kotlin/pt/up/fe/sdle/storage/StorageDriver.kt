package pt.up.fe.sdle.storage

/**
 * The type of keys used to store data.
 */
typealias StorageKey = String

/**
 * A [StorageDriver] is responsible for storing data of type [T] supplied to it.
 *
 * Implementations are responsible for defining the mechanisms by which that action is made.
 *
 * Examples of implementations include [FileSystemStorageDriver] and [MemoryStorageDriver]
 *
 * @param [T] The type of data to store
 */
sealed interface StorageDriver<T> {
    /**
     * Stores the given piece of data, optionally using the supplied [StorageKey].
     *
     * @param [data] the data to store
     * @param [key] the key under which to store data
     * @return whether this operation succeeded
     */
    fun store(
        key: StorageKey,
        data: T,
    ): Boolean

    /**
     * Retrieves the data of type [T] stored under the given [StorageKey].
     *
     * @param [key] the key under which the desired data is stored,
     * @return the data item, or null if not found.
     */
    fun retrieve(key: StorageKey): T?
}
