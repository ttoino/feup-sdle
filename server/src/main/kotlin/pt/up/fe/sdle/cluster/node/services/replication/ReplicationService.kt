package pt.up.fe.sdle.cluster.node.services.replication

import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageKey

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
    suspend fun replicatePut(key: StorageKey, data: ShoppingList): Int

    /**
     * Replicates a GET operation for data stored under [key]
     *
     * @param key The under which the desired data is stored
     * @return The number of successful replicas
     */
    suspend fun replicateGet(key: StorageKey): Int
}