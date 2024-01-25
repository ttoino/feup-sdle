package pt.up.fe.sdle.cluster.node.services.replication

import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageKey

/**
 * Result of replicating a value to another nodes of this nodes cluster.
 */
data class ReplicatedValue(
    /**
     * The replica of the data sent
     */
    val data: ShoppingList?,
    /**
     * Whether the replicated operation succeeded
     */
    val success: Boolean = true,
)

/**
 * Service responsible for replicating data on a node.
 */
interface ReplicationService {
    /**
     * Replicates a PUT operation of [data] using the given [key].
     *
     * @param key The key under which to store replicas of [data].
     * @param data The data to replicate.
     * @return The number of successful replicas.
     */
    suspend fun replicatePut(
        key: StorageKey,
        data: ShoppingList,
    ): List<ReplicatedValue>

    /**
     * Replicates a GET operation for data stored under [key]
     *
     * @param key The under which the desired data is stored
     * @return The number of successful replicas
     */
    suspend fun replicateGet(key: StorageKey): List<ReplicatedValue>
}
