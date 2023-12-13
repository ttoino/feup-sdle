@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pt.up.fe.sdle.api.GetResponse
import pt.up.fe.sdle.api.SyncPayload
import pt.up.fe.sdle.api.SyncResponse
import pt.up.fe.sdle.cluster.ClusterNode
import pt.up.fe.sdle.cluster.node.services.bootstrap.DummyBootstrapService
import pt.up.fe.sdle.cluster.node.services.gossip.NodeGossipProtocolService
import pt.up.fe.sdle.cluster.node.services.handoff.Hint
import pt.up.fe.sdle.cluster.node.services.handoff.HintedHandoffService
import pt.up.fe.sdle.cluster.node.services.handoff.NodeHintedHandoffService
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.logger
import pt.up.fe.sdle.storage.StorageKey
import java.util.*

/**
 * Represents a peer node in the context of this running instance.
 *
 * All calls result in a network RPC to the actual node that should handle the request.
 */
class RemoteNode(
    address: String = "0.0.0.0",
    id: NodeID = UUID.randomUUID().toString(),
) : Node(address, id) {
    override val gossipService = NodeGossipProtocolService(this, httpClient)
    override val bootstrapService = DummyBootstrapService()
    override val hintService: HintedHandoffService = NodeHintedHandoffService()

    override suspend fun put(
        key: StorageKey,
        data: ShoppingList,
        replica: Boolean,
    ): ShoppingList {
        if (replica) {
            logger.info("Attempting to replicate PUT call to node with id $id at address $address")
        } else {
            logger.info("Received delegated PUT call for node with id $id at address $address")
        }

        coroutineScope {
            launch {
                this@RemoteNode.gossipService.propagateClusterStatus(this@RemoteNode)
            }
        }

        val payload = SyncPayload(data, replica = replica)

        val response: HttpResponse
        val endpoint = "http://$address/list/${data.id}"
        try {

            response =
                httpClient.post(endpoint) {
                    contentType(ContentType.Application.Json)
                    setBody(payload)
                }
        } catch (e: Exception) {
            when (e) {
                is ConnectTimeoutException,
                is HttpRequestTimeoutException,
                -> {
                    // Couldn't reach node, mark it as unavailable
                    logger.error("Node with id $id and address $address is unreachable", e)

                    node.cluster.updateNodeStatus(ClusterNode(this.id, this.address, false))

                    // Store a hint for this node
                    hintService.storeHint(Hint(this, data, replica))
                }

                else -> logger.error("Unknown network error", e)
            }

            throw e
        }

        if (!response.status.isSuccess()) {
            // TODO: Handle request failure

            logger.error("Unknown application error: ${response.status}; Endpoint: $endpoint")

            return data
        }

        node.cluster.updateNodeStatus(ClusterNode(this.id, this.address, true))

        val responseData = response.body<SyncResponse>()

        return responseData.list
    }

    override suspend fun get(
        key: StorageKey,
        replica: Boolean,
    ): ShoppingList? {
        if (replica) {
            logger.info("Attempting to replicate GET call to node with id $id at address $address")
        } else {
            logger.info("Received delegated GET call for node with id $id at address $address")
        }

        coroutineScope {
            launch {
                this@RemoteNode.gossipService.propagateClusterStatus(this@RemoteNode)
            }
        }

        val response: HttpResponse
        val endpoint = "http://$address/list/$key"
        try {
            response =
                httpClient.get(endpoint) {
                    url {
                        parameters.append("replica", replica.toString())
                    }
                    accept(ContentType.Application.Json)
                }
        } catch (e: Exception) {
            // TODO: "Network error"

            when (e) {
                is ConnectTimeoutException,
                is HttpRequestTimeoutException,
                -> {

                    logger.error("Node with id $id and address $address is unreachable", e)

                    // Couldn't reach node, mark it as unavailable
                    node.cluster.updateNodeStatus(ClusterNode(this.id, this.address, false))
                }

                else -> logger.error("Unknown network error", e)
            }

            throw e
        }

        if (!response.status.isSuccess()) {
            // This should not happen for "normal" response codes

            logger.error("Unknown application error: ${response.status}; Endpoint: $endpoint")

            return null
        }

        node.cluster.updateNodeStatus(ClusterNode(this.id, this.address, true))

        val responseData = response.body<GetResponse>()

        return responseData.list
    }
}
