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
        val results: MutableList<ReplicatedValue> = mutableListOf()
        coroutineScope {

            val preferenceList = node.cluster.getReplicationNodesFor(data.id)

            val replicationAmount = node.cluster.getReplicationAmount()
            var replicated = 0

            val replicaIterator = preferenceList.iterator()

            while (replicaIterator.hasNext() && replicated < replicationAmount) {
                val replicationNode = replicaIterator.next()

                try {
                    val replicaList = replicationNode.put(key, data, true)

                    replicated++
                    results.add(ReplicatedValue(replicaList, true))
                } catch (e: Exception) {
                    when (e) {
                        is ConnectTimeoutException,
                        is HttpRequestTimeoutException,
                        -> {
                            // Couldn't reach node, mark it as unavailable
                            node.cluster.updateNodeStatus(
                                ClusterNode(
                                    replicationNode.id,
                                    replicationNode.address,
                                    false
                                )
                            )

                            // Store a hint so that the hint service tries to re-send it
                            // hintService.storeHint(Hint(it, data, true)) this is already handled by the node since it should be a RemoteNode
                        }
                    }
                }
            }

            // results =
            //     node.cluster.getReplicationNodesFor(node).map {
            //         try {
            //             ReplicatedValue(it.put(key, data, true), true)
            //         } catch (e: Exception) {
            //             when (e) {
            //                 is ConnectTimeoutException,
            //                 is HttpRequestTimeoutException,
            //                 -> {
            //                     // Couldn't reach node, mark it as unavailable
            //                     node.cluster.updateNodeStatus(ClusterNode(it.id, it.address, false))
//
            //                     // Store a hint so that the hint service tries to re-send it
            //                     // hintService.storeHint(Hint(it, data, true)) this is already handled by the node since it should be a RemoteNode
            //                 }
            //             }
//
            //             ReplicatedValue(null, false)
            //         }
            //     }
        }

        return results.toList()
    }

    override suspend fun replicateGet(key: StorageKey): List<ReplicatedValue> {
        val results: MutableList<ReplicatedValue> = mutableListOf()
        coroutineScope {

            val preferenceList = node.cluster.getReplicationNodesFor(key)

            val replicationAmount = node.cluster.getReplicationAmount()
            var replicated = 0

            val replicaIterator = preferenceList.iterator()

            while (replicaIterator.hasNext() && replicated < replicationAmount) {
                val replicationNode = replicaIterator.next()

                try {
                    val replicaList = replicationNode.get(key, true)

                    replicated++
                    results.add(ReplicatedValue(replicaList, true))
                } catch (e: Exception) {
                    when (e) {
                        is ConnectTimeoutException,
                        is HttpRequestTimeoutException,
                        -> {
                            // Couldn't reach node, mark it as unavailable
                            node.cluster.updateNodeStatus(
                                ClusterNode(
                                    replicationNode.id,
                                    replicationNode.address,
                                    false
                                )
                            )

                            // Store a hint so that the hint service tries to re-send it
                            // hintService.storeHint(Hint(it, data, true)) this is already handled by the node since it should be a RemoteNode
                        }
                    }
                }
            }

            // results =
            //     node.cluster.getReplicationNodesFor(node).map {
            //         try {
            //             ReplicatedValue(it.get(key, true), true)
            //         } catch (e: Exception) {
            //             when (e) {
            //                 is ConnectTimeoutException,
            //                 is HttpRequestTimeoutException,
            //                 -> {
            //                     // Couldn't reach node, mark it as unavailable
            //                     node.cluster.updateNodeStatus(ClusterNode(it.id, it.address, false))
            //                 }
            //             }
//
            //             ReplicatedValue(null, false)
            //         }
            //     }
        }

        return results.toList()
    }
}
