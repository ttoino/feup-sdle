@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageDriverFactory
import pt.up.fe.sdle.storage.StorageKey
import java.util.*
import pt.up.fe.sdle.cluster.cluster as _cluster

/**
 * Represents a unique identifier for this node.
 */
typealias NodeID = String

/**
 * The global [Node] instance that is unique to this instance of the server.<
 */
lateinit var node: Node

/**
 * A storage node responsible for providing backup storage for the application.
 */
abstract class Node protected constructor(
    private val _address: String = "0.0.0.0",
    private val _id: NodeID = UUID.randomUUID().toString(),
) {
    /**
     * The ID of this node.
     */
    val id: String get() = _id

    /**
     * The address on which this node listens to cluster requests.
     */
    val address: String get() = _address

    /**
     * The cluster this node belongs to.
     *
     * This [Cluster] object represents a view of the cluster as a whole and is not de de-facto cluster instance.
     */
    protected val cluster: Cluster get() = _cluster

    /**
     * HTTP Client used by a node to communicate with other nodes in its cluster.
     */
    protected var httpClient: HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

    companion object {
        /**
         * Whether the local node has already been initialized.
         */
        private var localNodeInitialized = false

        /**
         * The maximum number of retries to try before giving up on connecting to a cluster.
         */
        val MAX_RETRIES = System.getenv("CLUSTER_CONNECT_MAX_RETRIES")?.toInt() ?: 3

        /**
         * The minimum amount of time, in milliseconds, to wait before attempting to reconnect to the given cluster IP.
         *
         * This value is used in conjunction with an integer that goes from 0 to [MAX_RETRIES] to provide an exponential backoff mechanism.
         */
        val MIN_TIMEOUT_MS: Long = System.getenv("CLUSTER_CONNECT_MIN_TIMEOUT")?.toLong() ?: 500

        /**
         * Creates a new Node object using the default storage driver.
         */
        fun new(): Node {
            return if (!localNodeInitialized) {
                // This is the first call of this method for this physical node, create its singleton

                val driver = StorageDriverFactory.getDriver<ShoppingList>()

                localNodeInitialized = true
                node = LocalNode(driver)
                node
            } else {
                RemoteNode()
            }
        }

        /**
         * Creates a new Node object using the default storage driver and with the given id.
         *
         * @param id the id of the node to create
         */
        fun newWithId(id: NodeID): Node {
            return if (!localNodeInitialized) {
                // This is the first call of this method for this physical node, create its singleton

                val driver = StorageDriverFactory.getDriver<ShoppingList>()

                localNodeInitialized = true
                node = LocalNode(driver, id = id)
                node
            } else {
                RemoteNode(id = id)
            }
        }

        /**
         * Creates a new Node object using the default storage driver and with the given IP address.
         *
         * @param address the IP address of the new node
         */
        fun newWithAddress(address: String): Node {
            return if (!localNodeInitialized) {
                // This is the first call of this method for this physical node, create its singleton

                val driver = StorageDriverFactory.getDriver<ShoppingList>()

                localNodeInitialized = true
                node = LocalNode(driver, address = address)
                node
            } else {
                RemoteNode(address = address)
            }
        }

        /**
         * Creates a new Node object using the default storage driver and with the given id and IP address.
         *
         * @param id the id of the node to create
         * @param address the IP address of the new node
         */
        fun newWith(
            id: NodeID,
            address: String,
        ): Node {
            return if (!localNodeInitialized) {
                // This is the first call of this method for this physical node, create its singleton

                val driver = StorageDriverFactory.getDriver<ShoppingList>()

                localNodeInitialized = true
                node = LocalNode(driver, id = id, address = address)
                node
            } else {
                RemoteNode(id = id, address = address)
            }
        }
    }

    /**
     * Bootstraps this node.
     */
    abstract suspend fun bootstrap()

    /**
     * Begins a PUT operation on this node, storing the give [data] under the given [key].
     *
     * It might or might be this node the one responsible for storing the given [data].
     *
     * @param [key] The key under which to store [data]
     * @param [data] The data to store
     * @param [replica] Whether this operation happened due to a replication event or not
     * @return The modified shopping list
     */
    abstract suspend fun put(
        key: StorageKey,
        data: ShoppingList,
        replica: Boolean
    ): ShoppingList

    /**
     * Begins a GET operation on this node, returning the data item stored under the given [key].
     *
     * It might or might be this node the one responsible for executing this operation.
     *
     * @param [key] the key under which the desired shopping list is stored
     * @param [replica] Whether this operation happened due to a replication event or not
     * @return the desired shopping list, or  if not found
     */
    abstract suspend fun get(
        key: StorageKey,
        replica: Boolean
    ): ShoppingList?
}
