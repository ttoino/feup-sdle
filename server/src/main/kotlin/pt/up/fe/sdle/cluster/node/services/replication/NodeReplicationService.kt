package pt.up.fe.sdle.cluster.node.services.replication

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    override suspend fun replicate(key: StorageKey, data: ShoppingList) {
        coroutineScope {
            node.cluster.getReplicationNodesFor(node).forEach {
                launch {
                    it.put(key, data, true)
                }
            }
        }
    }
}