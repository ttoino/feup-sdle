@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster

import kotlinx.serialization.Serializable
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.cluster.node.NodeID
import pt.up.fe.sdle.cluster.node.node
import pt.up.fe.sdle.logger
import pt.up.fe.sdle.storage.FileSystemStorageDriver
import java.security.MessageDigest
import java.util.*
import kotlin.math.min

/**
 * The global cluster object, unique for this instance.
 */
val cluster = Cluster()

/**
 * A view of a node of this cluster.
 */
@Serializable
data class ClusterNode(
    /**
     * The corresponding node id.
     */
    val nodeId: NodeID,
    /**
     * The address of the node.
     */
    val nodeAddress: String,
    /**
     * Whether the given node is alive
     */
    val isAlive: Boolean,
)

/**
 * A [Cluster] represents a view over a collection of nodes that are logically connected.
 *
 * Each [Cluster] instance, while belonging to a single node, has enough information for any given node to work on its peers.
 */
class Cluster {
    // TODO: change to use ClusterNode
    private val nodeRing: TreeMap<Long, Triple<Node, Boolean, Boolean>> = TreeMap()

    /**
     * The nodes present in this cluster.
     */
    val nodes get() = nodeRing.toSortedMap()

    private val lock: Any = Any()

    private val virtualNodeAmount = System.getenv("CLUSTER_VIRTUAL_NODE_AMOUNT")?.toInt() ?: 3

    private val hasher = Hasher()

    /**
     * Generates the hashes used by the virtual nodes of a given node.
     *
     * @param node The node whose virtual node hashes we want.
     * @return The hashes for the virtual nodes that [node] generates.
     */
    private fun generateVirtualNodeHashes(node: Node) = generateVirtualNodeHashes(node.id)

    /**
     * Generates the hashes used by the virtual nodes of a given node using the node's id.
     *
     * @param id The id of the node whose virtual node hashes we want.
     * @return The hashes for the virtual nodes that a node with [id] generates.
     */
    private fun generateVirtualNodeHashes(id: NodeID) = List(virtualNodeAmount) { hasher.generateHash("$id-vn$it") }

    /**
     * Adds a new node to this cluster's node ring.
     *
     * @param node The node to add.
     */
    fun addNode(node: Node) {
        val nodeHash = hasher.generateHash(node.id)
        val virtualNodesHashes = generateVirtualNodeHashes(node)

        val alive = true

        synchronized(lock) {
            nodeRing[nodeHash] = Triple(node, alive, false)
            virtualNodesHashes.forEach { nodeRing[it] = Triple(node, alive, true) }
        }
    }

    /**
     * Removes the given node from this cluster's node ring.
     *
     * @param node The node to remove
     */
    fun removeNode(node: Node) {
        val nodeHash = hasher.generateHash(node.id)
        val virtualNodesHashes = generateVirtualNodeHashes(node)

        synchronized(lock) {
            nodeRing.remove(nodeHash)
            virtualNodesHashes.forEach { nodeRing.remove(it) }
        }
    }

    /**
     * Removes the given node from this cluster's node ring using its id.
     *
     * @param nodeId The id of the node to remove
     */
    fun removeNode(nodeId: NodeID) {
        val nodeHash = hasher.generateHash(nodeId)
        val virtualNodesHashes = generateVirtualNodeHashes(nodeId)

        synchronized(lock) {
            nodeRing.remove(nodeHash)
            virtualNodesHashes.forEach { nodeRing.remove(it) }
        }
    }

    /**
     * Returns the node responsible for handling a given key, or null if it does not exist or is marked as "not-alive", i.e., the last heartbeat to that node timed out.
     *
     * @param key the id to hash
     * @return The corresponding node or null if: 1) it does not exist; 2) it exists but is marked "dead" (due to being unreachable)
     */
    fun getNodeFor(key: String): Node? {
        if (nodeRing.isEmpty()) {
            return null
        }

        var hash = hasher.generateHash(key)

        if (hash !in nodeRing) {
            val tailMap = nodeRing.tailMap(hash)

            hash = if (tailMap.isEmpty()) nodeRing.firstKey() else tailMap.firstKey()
        }

        val (node, alive) = nodeRing[hash]!!

        return if (alive) node else null
    }

    /**
     * Gets all physical nodes responsible for handling replicas of data for the given node.
     *
     * @param node The node whose replica nodes we want
     * @return The replica nodes for the given node
     */
    fun getReplicationNodesFor(node: Node): List<Node> {
        // TODO: see if this method might become a bottleneck

        // Filter only for the non-virtual nodes
        // If there's only one virtual node, return early
        val physicalNodes = nodeRing.filter { !it.value.third }.takeIf { it.size > 1 }?.toSortedMap() ?: return listOf()

        val actualReplicationAmount = getReplicationAmount()

        val nodeHash = hasher.generateHash(node.id)
        val orderedFollowNodes =
            physicalNodes.tailMap(nodeHash + 1) // since 'tailMap' is lower-end inclusive, offset hash to exclude the current node

        val followNodes: List<Node> =
            if (orderedFollowNodes.isEmpty()) {
                // The given node is the last node on the ring, loop around.

                physicalNodes.tailMap(physicalNodes.firstKey()).map { it.value.first }
            } else if (orderedFollowNodes.size < actualReplicationAmount) {
                // we would get less replication nodes than what we are supposed to, add remaining nodes.
                // Even if we add ourselves we won't be added to the replica set because of the -1 in the 'actualReplicationAmount' calculation

                orderedFollowNodes.values.map { it.first }.toList() +
                    physicalNodes.tailMap(physicalNodes.firstKey())
                        .map { it.value.first }.toList()
            } else {
                // We have enough nodes, extract them from map values
                orderedFollowNodes.values.map { it.first }
            }

        return followNodes.take(actualReplicationAmount)
    }

    /**
     * Gets a list ordered by preference of replication for a given key. The first elements is always the node holding the key.
     *
     * @return A list of nodes that are responsible for replicating the given key
     */
    fun getReplicationNodesFor(id: NodeID): List<Node> {
        val physicalNodes = nodeRing.filter { !it.value.third }.takeIf { it.size > 1 }?.toSortedMap() ?: return listOf()

        val hash = hasher.generateHash(id)

        return physicalNodes.tailMap(hash).map { it.value.first }.toList() +
            physicalNodes.headMap(hash)
                .map { it.value.first }.toList()
    }

    /**
     * Updates the live-ness status of [node].
     *
     * @param node The node whose status on this cluster we want to update.
     */
    fun updateNodeStatus(node: ClusterNode) {
        val nodeId = node.nodeId
        val nodeAddress = node.nodeAddress
        val status = node.isAlive

        val nodeHash = hasher.generateHash(nodeId)
        val virtualNodesHashes = generateVirtualNodeHashes(nodeId)

        synchronized(lock) {
            // TODO: what if this is the physical node
            nodeRing[nodeHash]?.let {
                var updatedClusterNode = it.copy(second = status)

                if (it.first.address.startsWith("0.0.0.0") && it.first.id !== pt.up.fe.sdle.cluster.node.node.id) {
                    val clusterNode = updatedClusterNode.first

                    updatedClusterNode = updatedClusterNode.copy(first = Node.newWith(clusterNode.id, nodeAddress))
                }

                nodeRing.put(nodeHash, updatedClusterNode)
            } ?: run {
                nodeRing.putIfAbsent(nodeHash, Triple(Node.newWith(nodeId, nodeAddress), status, false))
            }

            virtualNodesHashes.forEach { hash ->
                nodeRing[hash]?.let {
                    var updatedClusterNode = it.copy(second = status)

                    if (it.first.address.startsWith("0.0.0.0") && it.first.id !== pt.up.fe.sdle.cluster.node.node.id) {
                        val clusterNode = updatedClusterNode.first

                        updatedClusterNode = updatedClusterNode.copy(first = Node.newWith(clusterNode.id, nodeAddress))
                    }

                    nodeRing.put(hash, updatedClusterNode)
                } ?: run {
                    nodeRing.putIfAbsent(hash, Triple(Node.newWith(nodeId, node.nodeAddress), status, true))
                }
            }
        }
    }

    /**
     * Updates the live-ness status of every node in [nodes].
     *
     * @param nodes the nodes whose statuses on this cluster want to update.
     */
    fun updateNodeStatuses(nodes: List<ClusterNode>) {
        synchronized(lock) {
            nodes.forEach { otherNode ->

                val nodeId = otherNode.nodeId
                val nodeAddress = otherNode.nodeAddress
                val status = otherNode.isAlive

                val nodeHash = hasher.generateHash(nodeId)
                val virtualNodesHashes = generateVirtualNodeHashes(nodeId)

                nodeRing[nodeHash]?.let {
                    var updatedClusterNode = it.copy(second = status)

                    if (it.first.address.startsWith("0.0.0.0") && it.first.id !== node.id) {
                        val clusterNode = updatedClusterNode.first

                        updatedClusterNode = updatedClusterNode.copy(first = Node.newWith(clusterNode.id, nodeAddress))
                    }

                    nodeRing.put(nodeHash, updatedClusterNode)
                } ?: run {
                    nodeRing.putIfAbsent(nodeHash, Triple(Node.newWith(nodeId, nodeAddress), status, false))
                }

                virtualNodesHashes.forEach { hash ->
                    nodeRing[hash]?.let {
                        var updatedClusterNode = it.copy(second = status)

                        if (it.first.address.startsWith("0.0.0.0") && it.first.id !== node.id) {
                            val clusterNode = updatedClusterNode.first

                            updatedClusterNode =
                                updatedClusterNode.copy(first = Node.newWith(clusterNode.id, nodeAddress))
                        }

                        nodeRing.put(hash, updatedClusterNode)
                    } ?: run {
                        nodeRing.putIfAbsent(hash, Triple(Node.newWith(nodeId, nodeAddress), status, true))
                    }
                }
            }
        }
    }

    /**
     * Returns the number of physical nodes in this cluster's node ring.
     *
     * @return The number of physical nodes in this cluster's node ring.
     */
    private fun getPhysicalNodeCount() = nodeRing.filter { !it.value.third }.size

    /**
     * Returns the actual replication amount for nodes in this cluster.
     * This is done to prevent a node from replicating data onto itself.
     *
     * @return The actual replication amount for this cluster.
     */
    fun getReplicationAmount() = min(getPhysicalNodeCount() - 1, Config.Node.REPLICATION_FACTOR)

    /**
     * Configuration values for this cluster
     */
    object Config {
        /**
         * Configuration options when bootstrapping a node.
         */
        object Bootstrap {
            /**
             * The initial cluster address to connect to.
             */
            val CONNECT_ADDRESS: String? = System.getenv("CONNECT_ADDRESS")

            /**
             * List of additional addresses to connect to when bootstrapping this node.
             */
            val CONNECT_ADDRESSES: List<String> = listOf()

            /**
             * The minimum amount of time, in milliseconds, to wait before attempting to reconnect to the given cluster IP.
             *
             * This value is used in conjunction with an integer that goes from 0 to [MAX_RETRIES] to provide an exponential backoff mechanism.
             */
            val MIN_TIMEOUT_MS: Long = System.getenv("CLUSTER_BOOTSTRAP_MIN_TIMEOUT")?.toLong() ?: 500

            /**
             * The maximum number of retries to try before giving up on connecting to a cluster.
             */
            val MAX_RETRIES = System.getenv("CLUSTER_BOOTSTRAP_MAX_RETRIES")?.toInt() ?: 3
        }

        /**
         * Storage layer configuration
         */
        object Storage {
            /**
             * The type of driver to return, computed from system configs.
             */
            val DRIVER_TYPE: String? = System.getenv("CLUSTER_STORAGE_DRIVER_TYPE")

            /**
             * Configuration values for the [FileSystemStorageDriver]
             */
            object FileSystem {
                /**
                 * The location where files are stored under
                 */
                val DATA_FILE_LOCATION: String = System.getenv("CLUSTER_STORAGE_DRIVER_FILESYSTEM_DATA_PATH") ?: "data"
            }
        }

        /**
         * Node functionality configuration
         */
        object Node {
            /**
             * The replication factor for nodes in this cluster.
             */
            val REPLICATION_FACTOR: Int get() = replicationFactor
            private var replicationFactor = System.getenv("CLUSTER_NODE_REPLICATION_FACTOR")?.toInt() ?: 1

            /**
             * Node communication quorum configurations
             */
            object Quorum {
                /**
                 * Whether the quorum mechanisms applied should be sloppy or not.
                 */
                val SLOPPY = System.getenv("CLUSTER_NODE_SLOPPY_QUORUM")?.toBoolean() ?: true

                /**
                 * The read quorum value for this cluster.
                 */
                val READ = System.getenv("CLUSTER_NODE_READ_QUORUM")?.toInt() ?: 1

                /**
                 * The write quorum value for this cluster.
                 */
                val WRITE = System.getenv("CLUSTER_NODE_WRITE_QUORUM")?.toInt() ?: 1

                init {
                    if (READ + WRITE <= REPLICATION_FACTOR) {
                        logger.warn(
                            "Invalid configuration: REPLICATION_FACTOR($REPLICATION_FACTOR) must be less than " +
                                "READ_QUORUM($READ) + WRITE_QUORUM($WRITE). Tuning REPLICATION_FACTOR",
                        )

                        replicationFactor = READ + WRITE - 1
                    }
                }
            }

            /**
             * Configuration for requests originating from a node.
             */
            object Request {
                /**
                 * Timeout in milliseconds for a request to complete.
                 */
                val TIMEOUT_MS = System.getenv("CLUSTER_NODE_REQUEST_TIMEOUT_MS")?.toLong() ?: 1500

                /**
                 * Timeout in milliseconds for a client to establish a connection.
                 */
                val CONNECT_TIMEOUT_MS = System.getenv("CLUSTER_NODE_CONNECT_TIMEOUT_MS")?.toLong() ?: 500
            }

            /**
             * Configuration for various node related services
             */
            object Services {
                /**
                 * Configuration for the HintedHandoff Service
                 */
                object HintedHandoff {
                    /**
                     * The delay in seconds before attempting to send another hinted request to its original node
                     */
                    val HINT_DELAY_S = System.getenv("CLUSTER_NODE_SERVICE_HINTS_DELAY_S")?.toInt() ?: 1
                }
            }
        }
    }

    companion object {
        private class Hasher {
            fun generateHash(key: String): Long {
                val hashAlgorithm = MessageDigest.getInstance("MD5")

                hashAlgorithm.update(key.toByteArray())

                val digest = hashAlgorithm.digest()

                return (digest[3].toLong() and 0xFF shl 24) or
                    (digest[2].toLong() and 0xFF shl 16) or
                    (digest[1].toLong() and 0xFF shl 8) or
                    (digest[0].toLong() and 0xFF)
            }
        }
    }
}
