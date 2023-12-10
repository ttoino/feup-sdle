@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster

import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.cluster.node.NodeID
import java.security.MessageDigest
import java.util.*
import kotlin.math.min

/**
 * The global cluster object, unique for this instance.
 */
val cluster = Cluster()

/**
 * A [Cluster] represents a view over a collection of nodes that are logically connected.
 *
 * Each [Cluster] instance, while belonging to a single node, has enough information for any given node to work on its peers.
 */
class Cluster {
    private val _nodes: TreeMap<Long, Triple<Node, Boolean, Boolean>> = TreeMap()

    /**
     * The nodes present in this cluster.
     */
    val nodes get() = _nodes.toSortedMap()

    private val lock: Any = Any()

    // TODO: retrieve this from configuration
    private val virtualNodeAmount = System.getenv("CLUSTER_VIRTUAL_NODE_AMOUNT")?.toInt() ?: 3

    private val hasher = Hasher()

    /**
     * Generates the hashes used by the virtual nodes of a given node.
     *
     * @param node The node whose virtual node hashes we want.
     * @return The hashes for the virtual nodes that [node] generates.
     */
    private fun generateVirtualNodeHashes(node: Node) =
        List(virtualNodeAmount) { hasher.generateHash("${node.id}-vn$it") }

    /**
     * Generates the hashes used by the virtual nodes of a given node using the node's id.
     *
     * @param id The id of the node whose virtual node hashes we want.
     * @return The hashes for the virtual nodes that a node with [id] generates.
     */
    private fun generateVirtualNodeHashes(id: NodeID) =
        List(virtualNodeAmount) { hasher.generateHash("$id-vn$it") }

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
            _nodes[nodeHash] = Triple(node, alive, false)
            virtualNodesHashes.forEach { _nodes[it] = Triple(node, alive, true) }
        }

        for ((hash, server) in _nodes) {
            val (_node, _alive, virtual) = server
            println("$hash -> Node{id=${_node.id}, alive=$_alive}, virtual=$virtual")
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
            _nodes.remove(nodeHash)
            virtualNodesHashes.forEach { _nodes.remove(it) }
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
            _nodes.remove(nodeHash)
            virtualNodesHashes.forEach { _nodes.remove(it) }
        }
    }

    /**
     * Returns the node responsible for handling a given key, or null if it does not exist or is marked as "not-alive", i.e., the last heartbeat to that node timed out.
     *
     * @param key the id to hash
     * @return The corresponding node or null if: 1) it does not exist; 2) it exists but is marked "dead" (due to being unreachable)
     */
    fun getNodeFor(key: String): Node? {
        if (_nodes.isEmpty()) {
            return null
        }

        var hash = hasher.generateHash(key)

        if (hash !in _nodes) {
            val tailMap = _nodes.tailMap(hash)

            hash = if (tailMap.isEmpty()) _nodes.firstKey() else tailMap.firstKey()
        }

        val (node, alive) = _nodes[hash]!!

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
        val physicalNodes = _nodes.filter { !it.value.third }.takeIf { it.size > 1 }?.toSortedMap() ?: return listOf()

        val actualReplicationAmount = getReplicationAmount()

        val nodeHash = hasher.generateHash(node.id)
        val orderedFollowNodes =
            physicalNodes.tailMap(nodeHash + 1) // since 'tailMap' is lower-end inclusive, offset hash to exclude the current node

        var followNodes: List<Node> = if (orderedFollowNodes.isEmpty()) {
            // The given node is the last node on the ring, loop around.

            physicalNodes.tailMap(physicalNodes.firstKey()).map { it.value.first }
        } else if (orderedFollowNodes.size < actualReplicationAmount) {
            // we would get less replication nodes than what we are supposed to, add remaining nodes.
            // Even if we add ourselves we won't be added to the replica set because of the -1 in the 'actualReplicationAmount' calculation

            orderedFollowNodes.values.map { it.first }.toList() + physicalNodes.tailMap(physicalNodes.firstKey())
                .map { it.value.first }.toList()
        } else {
            // We have enough nodes, extract them from map values
            orderedFollowNodes.values.map { it.first }
        }

        val replicas = mutableListOf<Node>()

        for (i in 0.until(actualReplicationAmount)) {

            // Since we double-checked that followNodes is not empty, this will never throw
            val nextNode = followNodes.first()

            replicas.add(nextNode)

            followNodes = followNodes.drop(0)
        }

        return replicas
    }

    /**
     * Returns the actual replication amount for nodes in this cluster.
     * This is done to prevent a node from replicating data onto itself.
     *
     * @return The actual replication amount for this cluster.
     */
    fun getReplicationAmount() = min(_nodes.filter { !it.value.third }.size - 1, REPLICATION_FACTOR)

    companion object {

        /**
         * The replication factor for nodes
         */
        val REPLICATION_FACTOR = System.getenv("CLUSTER_NODE_REPLICATION_FACTOR")?.toInt() ?: 3

        private class Hasher {
            private val hashAlgorithm = MessageDigest.getInstance("MD5")

            fun generateHash(key: String): Long {
                hashAlgorithm.reset()
                hashAlgorithm.update(key.toByteArray())

                val digest = hashAlgorithm.digest()

                return (digest[3].toLong() and 0xFF shl 24) or (digest[2].toLong() and 0xFF shl 16) or (digest[1].toLong() and 0xFF shl 8) or (digest[0].toLong() and 0xFF)
            }
        }
    }
}
