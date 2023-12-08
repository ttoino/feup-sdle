package pt.up.fe.sdle.storage

/**
 * The type of keys used to store data.
 */
typealias StorageKey = String

/**
 * A [StorageDriver] is responsible for storing data supplied to it.
 *
 * Implementations are responsible for defining the mechanisms by which that action is made.
 *
 * Examples of implementations include [FileSystemStorageDriver] and [MemoryStorageDriver]
 */
interface StorageDriver {

    /**
     * Stores the given piece of data, optionally using the supplied [StorageKey].
     *
     * @param [data] the data to store
     * @param [key] the key under which to store data
     */
    fun store(data: Any, key: StorageKey?)
}
