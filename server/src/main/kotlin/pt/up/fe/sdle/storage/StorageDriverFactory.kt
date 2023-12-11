package pt.up.fe.sdle.storage

import pt.up.fe.sdle.crdt.ShoppingList

/**
 * Factory that returns a new [StorageDriver] according to system configurations.
 *
 * By default, [MemoryStorageDriver] is returned if no valid configuration was found.
 */
sealed class StorageDriverFactory {
    companion object {
        /**
         * The type of driver to return, computed from system configs.
         */
        private val DRIVER_TYPE = System.getenv("DRIVER_TYPE")

        /**
         * Returns a [StorageDriver] for type [ShoppingList] according to [DRIVER_TYPE].
         *
         * @return A new [StorageDriver] for type [ShoppingList]
         */
        fun getDriver(): StorageDriver =
            when (DRIVER_TYPE?.lowercase()) {
                "file_system" -> FileSystemStorageDriver()
                "memory" -> MemoryStorageDriver()
                else -> MemoryStorageDriver() // TODO: add more options
            }
    }
}
