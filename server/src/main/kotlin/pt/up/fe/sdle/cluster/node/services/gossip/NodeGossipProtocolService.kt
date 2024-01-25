package pt.up.fe.sdle.cluster.node.services.gossip

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import pt.up.fe.sdle.api.ClusterSyncPayload
import pt.up.fe.sdle.cluster.ClusterNode
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.logger

/**
 * Service that initiates a gossip transaction with a peer node to exchange cluster status information
 */
class NodeGossipProtocolService(private val node: Node, private val httpClient: HttpClient) : GossipProtocolService {
    override suspend fun propagateClusterStatus(gossipNode: Node) {
        val currentClusterNodes = node.cluster.nodes.values.filter { !it.third }

        val gossipNodes =
            currentClusterNodes.filter { it.first.id !== node.id && it.first.id !== gossipNode.id }

        val nodeAddress = gossipNode.address
        val clusterView =
            gossipNodes.map {
                ClusterNode(it.first.id, it.first.address, it.second)
            }

        try {
            val response =
                httpClient.post("http://$nodeAddress/cluster/sync") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ClusterSyncPayload(
                            clusterView,
                            ClusterNode(node.id, node.address, true),
                        ),
                    )
                }

            if (response.status.isSuccess()) {
                val payload = response.body<ClusterSyncPayload>()

                node.cluster.updateNodeStatus(payload.node.copy(nodeAddress = nodeAddress))
                node.cluster.updateNodeStatuses(payload.nodes)

                logger.info("Cluster view synced with node ${gossipNode.id}")
            }
        } catch (e: Exception) {
            when (e) {
                is ConnectTimeoutException,
                is HttpRequestTimeoutException,
                -> {
                    logger.warn("Node with id ${gossipNode.id} and address $nodeAddress is unreachable", e)

                    // Couldn't reach node, mark it as unavailable
                    cluster.updateNodeStatus(ClusterNode(gossipNode.id, gossipNode.address, false))
                }

                else -> {
                    logger.error(
                        "Unknown network error when propagating cluster view information to node at address $nodeAddress. " +
                            "Assuming it is unreachable",
                        e,
                    )

                    cluster.updateNodeStatus(ClusterNode(gossipNode.id, gossipNode.address, false))
                }
            }
        }
    }
}
