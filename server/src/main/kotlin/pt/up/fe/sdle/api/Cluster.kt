@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.cluster.node.NodeID
import pt.up.fe.sdle.cluster.node.node
import pt.up.fe.sdle.logger

/**
 * Request to join a cluster.
 */
@Serializable
data class JoinPayload(
    /**
     * The id of the node asking to join this cluster.
     */
    val nodeId: NodeID,
    /**
     * The address of the node asking to join this cluster.
     */
    val nodeAddress: String,
)

/**
 * Request to Leave a cluster.
 */
@Serializable
data class LeavePayload(
    /**
     * The id of the node asking to join this cluster.
     */
    val nodeId: NodeID,
)

/**
 * Cluster management endpoints.
 *
 * The endpoints present are:
 * - Join: an external node asking to join this node's cluster.
 * - Leave: an external node asking to leave this node's cluster.
 */
fun Route.loadClusterManagementRoutes() {
    post {
        val payload = call.receive<JoinPayload>()
        val nodeId = payload.nodeId
        val nodeAddress = payload.nodeAddress

        val joinedNode = Node.newWith(nodeId, nodeAddress)

        logger.info("Node with id $nodeId at address $nodeAddress joined our cluster")

        cluster.addNode(joinedNode)

        call.respond(HttpStatusCode.OK, JoinPayload(node.id, node.address))
    }

    get {

        @Serializable
        class SerializeNode(
            val nodeRingKey: Long,
            val id: NodeID,
            val address: String,
            val isVirtual: Boolean,
            val isAlive: Boolean,
            val replicas: List<Pair<NodeID, String>>?
        )

        val nodes = cluster.nodes.map {

            val key = it.key
            val value = it.value

            val (node, isAlive, isVirtual) = value

            if (isVirtual) return@map null

            val replicas = cluster.getReplicationNodesFor(node).map { node2 ->
                Pair(node2.id, node2.address)
            }

            SerializeNode(key, node.id, node.address, isVirtual, isAlive, replicas)
        }.filterNotNull()

        call.respond(HttpStatusCode.OK, nodes)
    }

    delete {
        val payload = call.receive<LeavePayload>()
        val nodeId = payload.nodeId

        cluster.removeNode(nodeId)

        call.respond(HttpStatusCode.OK)
    }
}
