@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

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
    private var storageDriver: StorageDriver,
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

        logger.info("Storing and merging list with id ${data.id}")

        val mergedShoppingList: ShoppingList
        val stored: Boolean
        synchronized(key) {
            val currentShoppingList = this.storageDriver.retrieve(key)

            mergedShoppingList = currentShoppingList?.merge(data) ?: data

            stored = this.storageDriver.store(key, mergedShoppingList)
        }

        if (!replica) replicationService.replicate(key, mergedShoppingList)

        if (stored) {
            logger.info("${if (replica) "REPLICA: " else ""}Stored merged list")
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
        // TODO: implement replication, hinted handoff

        return this.storageDriver.retrieve(key)
    }
}
