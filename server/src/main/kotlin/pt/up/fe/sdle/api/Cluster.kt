@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.cluster.ClusterNode
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.cluster.node.NodeID
import pt.up.fe.sdle.cluster.node.node
import pt.up.fe.sdle.logger

/**
 * Request to join this node's cluster
 */
@Serializable
data class ClusterJoinRequest(
    /**
     * The id of the node making this request
     */
    val nodeId: NodeID,
)

/**
 * Response to a cluster join request.
 */
@Serializable
data class ClusterJoinResponse(
    /**
     * The view of the cluster from the perspective of the node handling this request.
     */
    val nodes: List<ClusterNode>,
    /**
     * The id of the node that sent this request
     */
    val node: ClusterNode,
)

/**
 * Request to Leave a cluster.
 */
@Serializable
data class LeavePayload(
    /**
     * The id of the node asking to join leave cluster.
     */
    val nodeId: NodeID,
)

/**
 * Request to sync cluster view
 */
@Serializable
data class ClusterSyncPayload(
    /**
     * The nodes to sync
     */
    val nodes: List<ClusterNode>,
    /**
     * The node that handled this request
     */
    val node: ClusterNode,
)

/**
 * Cluster management endpoints.
 *
 * The endpoints present are:
 * - Join: an external node asking to join this node's cluster.
 * - Leave: an external node asking to leave this node's cluster.
 */
internal fun Route.loadClusterManagementRoutes() {
    post {
        val payload = call.receive<ClusterJoinRequest>()

        val nodeId = payload.nodeId

        val requestOrigin = call.request.origin
        val nodeIp = requestOrigin.remoteAddress
        val nodePort = requestOrigin.localPort // use this since all nodes, will (hopefully) live under the same port

        val nodeAddress = "$nodeIp:$nodePort"

        val joinedNode = Node.newWith(nodeId, nodeAddress)

        logger.info("Node with id $nodeId at address $nodeAddress joined our cluster")

        cluster.addNode(joinedNode)

        val currentClusterNodes = node.cluster.nodes.values.filter { !it.third }

        val otherNodes =
            currentClusterNodes.filter { it.first.id !== node.id && it.first.id !== joinedNode.id }

        val clusterView =
            otherNodes.map {
                ClusterNode(it.first.id, it.first.address, it.second)
            }

        call.respond(HttpStatusCode.OK, ClusterJoinResponse(clusterView, ClusterNode(node.id, node.address, true)))
    }

    get {
        @Serializable
        class SerializeNode(
            val nodeRingKey: Long,
            val id: NodeID,
            val address: String,
            val isVirtual: Boolean,
            val isAlive: Boolean,
            val isLocal: Boolean,
            val replicas: List<Pair<NodeID, String>>?,
        )

        val nodes =
            cluster.nodes.map {
                val key = it.key
                val value = it.value

                val (otherNode, isAlive, isVirtual) = value

                if (isVirtual) return@map null

                val replicas =
                    cluster.getReplicationNodesFor(otherNode).map { node2 ->
                        Pair(node2.id, node2.address)
                    }

                SerializeNode(
                    key,
                    otherNode.id,
                    otherNode.address,
                    isVirtual,
                    isAlive,
                    otherNode.id == node.id,
                    replicas
                )
            }.filterNotNull()

        call.respond(HttpStatusCode.OK, nodes)
    }

    post("/sync") {
        val payload = call.receive<ClusterSyncPayload>()

        val requestOrigin = call.request.origin
        val nodeIp = requestOrigin.remoteAddress
        val nodePort = requestOrigin.localPort

        cluster.updateNodeStatus(payload.node.copy(nodeAddress = "$nodeIp:$nodePort"))
        cluster.updateNodeStatuses(payload.nodes)

        val currentClusterNodes = node.cluster.nodes.values.filter { !it.third }
        
        val otherNodes =
            currentClusterNodes.filter { it.first.id !== node.id && it.first.id !== payload.node.nodeId }

        val clusterView =
            otherNodes.map {
                ClusterNode(it.first.id, it.first.address, it.second)
            }

        call.respond(HttpStatusCode.OK, ClusterSyncPayload(clusterView, ClusterNode(node.id, node.address, true)))
    }

    delete {
        val payload = call.receive<LeavePayload>()
        val nodeId = payload.nodeId

        cluster.removeNode(nodeId)

        call.respond(HttpStatusCode.OK)
    }
}
