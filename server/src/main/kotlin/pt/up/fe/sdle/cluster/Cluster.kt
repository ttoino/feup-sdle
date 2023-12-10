@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster

import pt.up.fe.sdle.cluster.node.Node
import java.security.MessageDigest
import java.util.*

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
    private val nodes: TreeMap<Long, Pair<Node, Boolean>> = TreeMap()
    private val lock: Any = Any()

    // TODO: retrieve this from configuration
    private val virtualNodeAmount = System.getenv("NODE_REPLICATION_FACTOR")?.toInt() ?: 3

    private val hasher = Hasher()

    /**
     * Adds a new node to this cluster's node ring.
     *
     * @param node The node to add.
     */
    fun addNode(node: Node) {
        val nodeHash = hasher.generateHash(node.id)
        val virtualNodesHashes = List(virtualNodeAmount) { hasher.generateHash("${node.id}-vn$it") }

        val alive = true

        synchronized(lock) {
            nodes[nodeHash] = Pair(node, alive)
            virtualNodesHashes.forEach { nodes[it] = Pair(node, alive) }
        }
    }

    /**
     * Removes the given node from this cluster's node ring.
     *
     * @param node The node to remove
     */
    fun removeNode(node: Node) {
        val nodeHash = hasher.generateHash(node.id)
        val virtualNodesHashes = List(virtualNodeAmount) { hasher.generateHash("${node.id}-vn$it") }

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

        if (!nodes.containsKey(hash)) {
            val tailMap = nodes.tailMap(hash)

            hash = if (tailMap.isEmpty()) nodes.firstKey() else tailMap.firstKey()
        }

        val (node, alive) = nodes[hash]!!

        return if (alive) node else null
    }

    companion object {

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
