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
    private val node: Node,

    /**
     * The service responsible for storing hints for a given node.
     */
    // private val hintService: HintedHandoffService
) : ReplicationService {
    override suspend fun replicatePut(
        key: StorageKey,
        data: ShoppingList,
    ): List<ReplicatedValue> {
        val results: List<ReplicatedValue>
        coroutineScope {
            results =
                node.cluster.getReplicationNodesFor(node).map {
                    try {
                        ReplicatedValue(it.put(key, data, true), true)
                    } catch (e: Exception) {
                        when (e) {
                            is ConnectTimeoutException,
                            is HttpRequestTimeoutException,
                            -> {
                                // Couldn't reach node, mark it as unavailable
                                node.cluster.updateNodeStatus(ClusterNode(it.id, it.address, false))

                                // Store a hint so that the hint service tries to re-send it
                                // hintService.storeHint(Hint(it, data, true)) this is already handled by the node since it should be a RemoteNode
                            }
                        }

                        ReplicatedValue(null, false)
                    }
                }
        }

        return results
    }

    override suspend fun replicateGet(key: StorageKey): List<ReplicatedValue> {
        val results: List<ReplicatedValue>
        coroutineScope {
            results =
                node.cluster.getReplicationNodesFor(node).map {
                    try {
                        ReplicatedValue(it.get(key, true), true)
                    } catch (e: Exception) {
                        when (e) {
                            is ConnectTimeoutException,
                            is HttpRequestTimeoutException,
                            -> {
                                // Couldn't reach node, mark it as unavailable
                                node.cluster.updateNodeStatus(ClusterNode(it.id, it.address, false))
                            }
                        }

                        ReplicatedValue(null, false)
                    }
                }
        }

        return results
    }
}
