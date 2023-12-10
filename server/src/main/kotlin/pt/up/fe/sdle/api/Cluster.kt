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
        
    }

    delete {
        call.respond(HttpStatusCode.OK)
    }

    loadHealthCheck {
        "current node is healthy"
    }
}
