package pt.up.fe.sdle.cluster

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.MemoryStorageDriver
import pt.up.fe.sdle.storage.StorageDriver
import pt.up.fe.sdle.storage.StorageKey
import java.util.*

/**
 * Represents a unique identifier for this node.
 */
typealias NodeID = String

/**
 * A storage node responsible for providing backup storage for the application.
 */
class Node private constructor(
    /**
     * The storage driver responsible for storing data on this node
     */
    private var storageDriver: StorageDriver<ShoppingList>,
    private val _address: String = "0.0.0.0",
    private val _id: NodeID = UUID.randomUUID().toString(),
) {
    /**
     *
     */
    val id: String get() = _id

    val address: String get() = _address

    /**
     * The cluster this node belongs to.
     *
     * This [Cluster] object represents a view of the cluster as a whole and is not de de-facto cluster instance.
     */
    lateinit var cluster: Cluster

    /**
     * HTTP Client used by a node to communicate with other nodes in its cluster.
     */
    private lateinit var httpClient: HttpClient
    init {
        httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

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

            val newInstance = Node(MemoryStorageDriver())

            if (_instance === null) {
                // This is the first call of this method for this physical node, create its singleton
                _instance = newInstance
            }

            return newInstance
        }

        /**
         * Creates a new Node object using the default storage driver and with the given id.
         *
         * @param id the id of the node to create
         */
        fun newWithId(id: NodeID): Node {

            // TODO: make storage driver configurable

            val newInstance = Node(MemoryStorageDriver(), _id = id)

            if (_instance === null) {
                // This is the first call of this method for this physical node, create its singleton and return it
                _instance = newInstance
            }

            return newInstance
        }

        /**
         * Creates a new Node object using the default storage driver and with the given IP address.
         *
         * @param address the IP address of the new node
         */
        fun newWithAddress(address: String): Node {

            // TODO: make storage driver configurable

            val newInstance = Node(MemoryStorageDriver(), _address = address)

            if (_instance === null) {
                // This is the first call of this method for this physical node, create its singleton and return it
                _instance = newInstance
            }

            return newInstance
        }

        /**
         * Creates a new Node object using the default storage driver and with the given id and IP address.
         *
         * @param id the id of the node to create
         * @param address the IP address of the new node
         */
        fun newWith(id: NodeID, address: String): Node {

            // TODO: make storage driver configurable

            val newInstance = Node(MemoryStorageDriver(), id, address)

            if (_instance === null) {
                // This is the first call of this method for this physical node, create its singleton and return it
                _instance = newInstance
            }

            return newInstance
        }
    }

    private var bootstrapped = false

    /**
     *
     */
    fun bootstrap() {
        if (bootstrapped) return

        this.cluster = Cluster()
        this.cluster.addNode(this)

        // TODO: make configurable
        val MAX_RETRIES = 3;
        val TIMEOUT: Long = 500

        val connectIp = System.getenv("CONNECT_ADDRESS")

        if (connectIp !== null) {
            // Issue a join request to the specified node

            var retries = 0

            @Serializable
            data class JoinPayload(var nodeId: String, var nodeAddress: String)

            // TODO: move this logic elsewhere
            while (retries++ < MAX_RETRIES) {
                println("Attempting to join cluster of node @ '$connectIp'")

                var response: HttpResponse

                runBlocking {
                    response = httpClient.post("$connectIp/cluster") {
                        contentType(ContentType.Application.Json)
                        setBody(JoinPayload(this@Node.id, this@Node.address))
                    }
                }

                if (response.status.isSuccess()) {
                    // Cluster join successful on the other side, mark this node as a member of the cluster

                    var responsePayload: JoinPayload
                    runBlocking {
                        responsePayload = response.body<JoinPayload>()
                    }

                    val otherNode = newWith(responsePayload.nodeId, responsePayload.nodeAddress)

                    this.cluster.addNode(otherNode)

                    bootstrapped = true

                    println(this.cluster.toString())

                    return
                }

                // Exponential Backoff
                Thread.sleep(retries * TIMEOUT)
            }

            println("Failed to join cluster of node @ '$connectIp'")
        }
    }

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
