@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.storage

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
         * Returns a [StorageDriver] for type [T] according to [DRIVER_TYPE].
         *
         * @param T The type of data to store using the given driver.
         * @return A new [StorageDriver] for type [T]
         */
        fun <T> getDriver(): StorageDriver<T> =
            when (DRIVER_TYPE?.lowercase()) {
                "file_system" -> FileSystemStorageDriver()
                "memory" -> MemoryStorageDriver()
                else -> MemoryStorageDriver() // TODO: add more options
            }
    }
}
