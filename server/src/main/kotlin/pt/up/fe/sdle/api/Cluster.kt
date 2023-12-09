@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.cluster.Node
import pt.up.fe.sdle.cluster.NodeID

/**
 * Registers a "health check" endpoint at the path this is called.
 */
fun Route.loadClusterManagementRoutes() {
    post {

        @Serializable
        data class JoinPayload(val nodeId: NodeID, val nodeAddress: String)

        val payload = call.receive<JoinPayload>()
        val nodeId = payload.nodeId
        val nodeAddress = payload.nodeAddress

        val joinedNode = Node.newWith(nodeId, nodeAddress)
        
        val thisNode = Node.instance
        thisNode.cluster.addNode(joinedNode)

        call.respond(HttpStatusCode.OK, JoinPayload(thisNode.id, thisNode.address))
    }

    loadHealthCheck {
        val nodeId = it.parameters["nodeId"]

        "$nodeId is healthy"
    }
}
