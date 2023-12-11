package pt.up.fe.sdle.cluster.node.services.gossip

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
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
        val clusterView = gossipNodes.map {
            ClusterNode(it.first.id, it.first.address, it.second)
        }

        try {
            val response = httpClient.post("http://$nodeAddress/cluster/sync") {
                contentType(ContentType.Application.Json)
                setBody(
                    ClusterSyncPayload(
                        clusterView, ClusterNode(node.id, node.address, true)
                    )
                )
            }

            if (response.status.isSuccess()) {
                val payload = response.body<ClusterSyncPayload>()

                node.cluster.updateNodeStatus(payload.node)
                node.cluster.updateNodeStatuses(payload.nodes)

                logger.info("Cluster view synced with node ${gossipNode.id}")
            }
        } catch (e: Exception) {
            logger.error("Unknown network error when propagating cluster view information to node at address $nodeAddress. Assuming it is unreachable")

            cluster.updateNodeStatus(ClusterNode(gossipNode.id, gossipNode.address, false))
        }
    }
}
