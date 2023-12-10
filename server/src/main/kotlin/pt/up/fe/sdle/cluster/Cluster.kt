@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster

import pt.up.fe.sdle.cluster.node.Node
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
    private val nodes: TreeMap<Long, Triple<Node, Boolean, Boolean>> = TreeMap()
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
     * Adds a new node to this cluster's node ring.
     *
     * @param node The node to add.
     */
    fun addNode(node: Node) {
        val nodeHash = hasher.generateHash(node.id)
        val virtualNodesHashes = generateVirtualNodeHashes(node)

        val alive = true

        synchronized(lock) {
            nodes[nodeHash] = Triple(node, alive, false)
            virtualNodesHashes.forEach { nodes[it] = Triple(node, alive, true) }
        }

        for ((hash, server) in nodes) {
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
            nodes.remove(nodeHash)
            virtualNodesHashes.forEach { nodes.remove(it) }
        }
    }

    /**
     * Returns the node responsible for handling a given key, or null if it does not exist or is marked as "not-alive", i.e., the last heartbeat to that node timed out.
     *
     * @param key the id to hash
     * @return The corresponding node or null if: 1) it does not exist; 2) it exists but is marked "dead" (due to being unreachable)
     */
    fun getNodeFor(key: String): Node? {
        if (nodes.isEmpty()) {
            return null
        }

        var hash = hasher.generateHash(key)

        if (hash !in nodes) {
            val tailMap = nodes.tailMap(hash)

            hash = if (tailMap.isEmpty()) nodes.firstKey() else tailMap.firstKey()
        }

        val (node, alive) = nodes[hash]!!

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
        val physicalNodes = nodes.filter { !it.value.third }.toSortedMap()

        val nodeHash = hasher.generateHash(node.id)
        var followNodes =
            physicalNodes.tailMap(nodeHash + 1) // since 'tailMap' is lower-end inclusive, offset hash to exclude the current node

        if (followNodes.isEmpty()) {
            // The given node is the last node on the ring, loop around.

            val firstHash = physicalNodes.firstKey()

            val nonFirstFollowNodes = physicalNodes.tailMap(physicalNodes.firstKey() + 1).apply {
                if (this.isEmpty()) {
                    // We loop around and then check for nodes that are not the first one.
                    // If this is empty, it means that there is only one physical node in the cluster, which should be the node passed in as argument.
                    // If that's the case, replication is not possible so just return early
                    return listOf()
                }
            }

            followNodes = nonFirstFollowNodes.plus(Pair(firstHash, physicalNodes[firstHash]!!)).toSortedMap()
        }

        val replicas = mutableListOf<Node>()

        val actualReplicationAmount = getReplicationAmount()

        for (i in 0.until(actualReplicationAmount)) {

            // Since we double-checked that followNodes is not empty, this will never throw
            val nextNodeHash = followNodes.firstKey()
            val (nextNode) = physicalNodes[nextNodeHash]!!

            replicas.add(nextNode)

            followNodes = physicalNodes.tailMap(nextNodeHash + 1)
        }

        return replicas
    }

    /**
     * Returns the actual replication amount for nodes in this cluster.
     * This is done to prevent a node from replicating data onto itself.
     *
     * @return The actual replication amount for this cluster.
     */
    fun getReplicationAmount() = min(nodes.filter { !it.value.third }.size - 1, REPLICATION_FACTOR)

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

                return (digest[3].toLong() and 0xFF shl 24) or
                        (digest[2].toLong() and 0xFF shl 16) or
                        (digest[1].toLong() and 0xFF shl 8) or
                        (digest[0].toLong() and 0xFF)
            }
        }
    }
}
