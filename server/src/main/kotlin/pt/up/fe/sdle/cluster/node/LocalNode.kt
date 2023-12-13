@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.cluster.node.services.bootstrap.BootstrapService
import pt.up.fe.sdle.cluster.node.services.bootstrap.NodeBootstrapService
import pt.up.fe.sdle.cluster.node.services.gossip.DummyGossipProtocolService
import pt.up.fe.sdle.cluster.node.services.gossip.GossipProtocolService
import pt.up.fe.sdle.cluster.node.services.handoff.HintedHandoffService
import pt.up.fe.sdle.cluster.node.services.handoff.NodeHintedHandoffService
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

    override val hintService: HintedHandoffService = NodeHintedHandoffService()

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

        var stored: Boolean
        synchronized(key) {
            val currentShoppingList = this.storageDriver.retrieve(key)

            mergedShoppingList = currentShoppingList?.merge(data) ?: data

            stored = this.storageDriver.store(key, mergedShoppingList)
        }

        if (!replica) {
            val replicas = replicationService.replicatePut(key, mergedShoppingList)

            val healthyReplicas = replicas.filter { it.success }

            if (healthyReplicas.size < Cluster.Config.Node.Quorum.WRITE - (if (stored) 1 else 0)) {
                logger.error("Not enough nodes to reach quorum!")

                throw Exception("Not enough nodes to reach quorum!")
            }

            if (Cluster.Config.Node.Quorum.SLOPPY) {
                logger.info("Write sloppy quorum reached! Returning own merged list.")

                return if (stored) mergedShoppingList else healthyReplicas.map { it.data }.first()!!

            } else {
                // TODO: implement stricter quorum semantics for PUT operations

                // FIXME: this is not the right solution but the compiler throws a fit
                return mergedShoppingList
            }
        } else {
            logger.info("REPLICA: Stored merged list")
            return mergedShoppingList
        }
    }

    override suspend fun get(
        key: StorageKey,
        replica: Boolean,
    ): ShoppingList? {
        logger.info("${if (replica) "REPLICA: " else ""}Retrieving list with id $key")

        val shoppingList: ShoppingList?
        synchronized(key) {
            shoppingList = this.storageDriver.retrieve(key)
        }

        if (!replica) {
            val replicas = replicationService.replicateGet(key)

            val healthyReplicas = replicas.filter { it.success } // Get replicas that succeeded

            // Here we just need to see if the replicas failed or not because we admit "null" as a valid value, so that cannot be how we measure quorum
            if (healthyReplicas.size < Cluster.Config.Node.Quorum.READ - 1) {
                logger.error("Not enough nodes to reach quorum!")

                throw Exception("Not enough nodes to reach quorum!")
            }

            if (Cluster.Config.Node.Quorum.SLOPPY) {
                logger.info("Read sloppy quorum reached! Returning retrieved list.")

                return shoppingList ?: healthyReplicas
                    .map { it.data } // Extract the result
                    .firstOrNull { it !== null } // Try to get the first non-null result
            } else {
                val groupedValues =
                    (healthyReplicas.map { it.data } + listOf(shoppingList)).groupBy {
                        it?.hashCode()?.toString() ?: "Null"
                    }

                if (groupedValues.none { (_, decision) -> decision.count() >= Cluster.Config.Node.Quorum.READ }) {
                    logger.error("Quorum not reached for any reads of value")

                    throw Exception("Quorum not reached for any reads of value")
                }

                logger.info("Read quorum reached! Returning retrieved list.")

                val (_, list) = groupedValues.maxOfWith({ entry1, entry2 ->
                    val (_, decision1) = entry1
                    val (_, decision2) = entry2

                    decision1.count().compareTo(decision2.count())
                }, { it })

                return list.firstOrNull()
            }
        } else {
            logger.info("REPLICA: Returning retrieved list")
            return shoppingList
        }
    }
}
