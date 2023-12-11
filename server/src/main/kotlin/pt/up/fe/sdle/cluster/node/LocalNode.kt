@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.cluster.node.services.bootstrap.BootstrapService
import pt.up.fe.sdle.cluster.node.services.bootstrap.NodeBootstrapService
import pt.up.fe.sdle.cluster.node.services.gossip.DummyGossipProtocolService
import pt.up.fe.sdle.cluster.node.services.gossip.GossipProtocolService
import pt.up.fe.sdle.cluster.node.services.replication.NodeReplicationService
import pt.up.fe.sdle.cluster.node.services.replication.ReplicationService
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.logger
import pt.up.fe.sdle.storage.StorageDriver
import pt.up.fe.sdle.storage.StorageKey
import java.util.*

/**
 * Represents the node object running on this instance, which is responsible for storing and retrieving data with a [StorageDriver].
 */
class LocalNode(
    /**
     * The storage driver responsible for storing data on this node
     */
    private val storageDriver: StorageDriver,
    address: String = "0.0.0.0",
    id: NodeID = UUID.randomUUID().toString(),
) : Node(address, id) {

    /**
     * Service responsible for replicating data for this node.
     */
    private val replicationService: ReplicationService = NodeReplicationService(this)
    override val bootstrapService: BootstrapService = NodeBootstrapService(this, httpClient)
    override val gossipService: GossipProtocolService = DummyGossipProtocolService()

    override suspend fun put(
        key: StorageKey,
        data: ShoppingList,
        replica: Boolean,
    ): ShoppingList {
        // TODO: implement hinted handoff

        logger.info("${if (replica) "REPLICA: " else ""}Storing and merging list with id ${data.id}")

        val mergedShoppingList: ShoppingList
        var stored = 0
        synchronized(key) {
            val currentShoppingList = this.storageDriver.retrieve(key)

            mergedShoppingList = currentShoppingList?.merge(data) ?: data

            stored += if (this.storageDriver.store(key, mergedShoppingList)) 1 else 0
        }

        if (!replica) stored += replicationService.replicatePut(key, mergedShoppingList)

        if (stored >= Cluster.WRITE_QUORUM) {
            logger.info("${if (replica) "REPLICA:" else "Quorum met!"} Stored merged list")
            return mergedShoppingList
        } else {
            logger.error("Failed to store data, returning previous version")
            return data
        }
    }

    override suspend fun get(
        key: StorageKey,
        replica: Boolean,
    ): ShoppingList? {
        // TODO: hinted handoff

        logger.info("${if (replica) "REPLICA: " else ""}Retrieving list with id $key")

        val shoppingList: ShoppingList?
        var retrieved = 0
        synchronized(key) {
            shoppingList = this.storageDriver.retrieve(key)
            retrieved += if (shoppingList !== null) 1 else 0
        }

        if (!replica) retrieved += replicationService.replicateGet(key)

        if (retrieved >= Cluster.READ_QUORUM) {
            logger.info("${if (replica) "REPLICA:" else "Quorum met!"} Returning retrieved list")
            return shoppingList
        } else {
            logger.error("Failed to store data, returning previous version")
            return null
        }
    }
}
