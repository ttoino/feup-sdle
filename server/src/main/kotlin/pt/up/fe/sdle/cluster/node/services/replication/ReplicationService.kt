package pt.up.fe.sdle.cluster.node.services.replication

import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageKey

/**
 * Service responsible for replicating data on a node.
 */
interface ReplicationService {

    /**
     * Replicates [data] using the given [key].
     *
     * @param key The key under which to store replicas of [data].
     * @param data The data to replicate.
     */
    suspend fun replicate(key: StorageKey, data: ShoppingList)
}