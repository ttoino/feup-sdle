package pt.up.fe.sdle.cluster.node.services.replication

import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.coroutineScope
import pt.up.fe.sdle.cluster.ClusterNode
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageKey

/**
 * Replicates data for a given [node]
 */
class NodeReplicationService(
    /**
     * The node for whom we want to replicate data
     */
    val node: Node
) : ReplicationService {


    override suspend fun replicatePut(key: StorageKey, data: ShoppingList): Int {

        val results: List<ShoppingList?>
        coroutineScope {
            results = node.cluster.getReplicationNodesFor(node).map {
                try {
                    it.put(key, data, true)
                } catch (e: Exception) {
                    when (e) {
                        is ConnectTimeoutException,
                        is HttpRequestTimeoutException -> {
                            // Couldn't reach node, mark it as unavailable
                            node.cluster.updateNodeStatus(ClusterNode(it.id, it.address, false))
                        }
                    }

                    null
                }
            }
        }

        return results.count { it !== null }
    }

    override suspend fun replicateGet(key: StorageKey): Int {
        val results: List<ShoppingList?>
        coroutineScope {
            results = node.cluster.getReplicationNodesFor(node).map {
                try {
                    it.get(key, true)
                } catch (e: Exception) {
                    when (e) {
                        is ConnectTimeoutException,
                        is HttpRequestTimeoutException -> {
                            // Couldn't reach node, mark it as unavailable
                            node.cluster.updateNodeStatus(ClusterNode(it.id, it.address, false))
                        }
                    }

                    null
                }
            }
        }

        return results.count { it !== null }
    }
}