package pt.up.fe.sdle.cluster

import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.MemoryStorageDriver
import pt.up.fe.sdle.storage.StorageDriver
import pt.up.fe.sdle.storage.StorageKey

/**
 * A storage node responsible for providing backup storage for the application.
 */
class Node(
    /**
     * The storage driver responsible for storing data on this node
     */
    private var storageDriver: StorageDriver<ShoppingList>,
    private var _peers: MutableList<String>,
) {
    /**
     * The list of peers that this node knows of.
     */
    val peers: List<String> get() = _peers.toList()

    private constructor(driver: StorageDriver<ShoppingList>) : this(driver, mutableListOf())

    companion object {
        @Volatile
        private var _instance: Node? = null

        /**
         * The node instance representing the physical node on which this instance is running.
         */
        val instance: Node get() = _instance as Node

        /**
         * Creates a new Node object using the default storage driver.
         */
        fun new(): Node {
            // TODO: make storage driver configurable

            if (_instance === null) {
                // This is the first call of this method for this physical node, create its singleton and return it

                _instance = Node(MemoryStorageDriver())

                return instance
            }

            return Node(MemoryStorageDriver())
        }
    }

    /**
     * Adds the given IP Address to this node's list of peers
     */
    fun addPeer(peer: String) = _peers.add(peer)

    /**
     * Begins a PUT operation on this node, storing the give [data] under the given [key].
     *
     * It might or might be this node the one responsible for storing the given [data].
     *
     * @param [key] the key under which to store [data]
     * @param [data] the data to store
     */
    fun put(
        key: StorageKey,
        data: ShoppingList,
    ) {
        // TODO: implement replication, hinted handoff

        this.storageDriver.store(key, data)
    }

    /**
     * Begins a GET operation on this node, returning the data item stored under the given [key].
     *
     * It might or might be this node the one responsible for executing this operation.
     *
     * @param [key] the key under which the desired shopping list is stored
     * @return the desired shopping list, or  if not found
     */
    fun get(key: StorageKey): ShoppingList? {
        // TODO: implement replication, hinted handoff

        return this.storageDriver.retrieve(key)
    }
}
