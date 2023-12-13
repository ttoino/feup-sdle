@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.cluster.node.services.bootstrap.BootstrapService
import pt.up.fe.sdle.cluster.node.services.gossip.GossipProtocolService
import pt.up.fe.sdle.cluster.node.services.handoff.HintedHandoffService
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
    private set

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
    val cluster: Cluster get() = _cluster

    /**
     * HTTP Client used by a node to communicate with other nodes in its cluster.
     */
    protected val httpClient: HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = Cluster.Config.Node.Request.TIMEOUT_MS
                connectTimeoutMillis = Cluster.Config.Node.Request.CONNECT_TIMEOUT_MS
            }
        }

    /**
     * Service responsible for handling the gossip-based membership discovery protocol of nodes.
     */
    protected abstract val gossipService: GossipProtocolService

    /**
     * Service responsible for bootstrapping this node.
     */
    protected abstract val bootstrapService: BootstrapService

    /**
     * Service responsible for managing hints generated at this node.
     */
    protected abstract val hintService: HintedHandoffService

    companion object {
        /**
         * Whether the local node has already been initialized.
         */
        private var localNodeInitialized = false

        /**
         * Creates a new Node object using the default storage driver.
         */
        fun new(): Node {
            return if (!localNodeInitialized) {
                // This is the first call of this method for this physical node, create its singleton

                val driver = StorageDriverFactory.getDriver()

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

                val driver = StorageDriverFactory.getDriver()

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

                val driver = StorageDriverFactory.getDriver()

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

                val driver = StorageDriverFactory.getDriver()

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
    suspend fun bootstrap() {
        this.cluster.addNode(this)

        bootstrapService.bootstrap()
    }

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
        replica: Boolean,
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
        replica: Boolean,
    ): ShoppingList?
}
