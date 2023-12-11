package pt.up.fe.sdle.cluster.node.services.gossip

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pt.up.fe.sdle.api.ClusterSyncPayload
import pt.up.fe.sdle.cluster.ClusterNode
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.logger

/**
 * A service implementing a membership gossip-based protocol for nodes on the cluster
 */
class GossipProtocolService(private val node: Node, private val httpClient: HttpClient) {

    /**
     * Initiates a gossip transaction with a peer node to exchange cluster status information
     */
    suspend fun propagateClusterStatus(gossipNode: Node) {
        coroutineScope {
            launch {
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

                        println(payload)

                        cluster.updateNodeStatus(payload.node)
                        cluster.updateNodeStatuses(payload.nodes)

                        logger.info("Cluster view synced with node ${gossipNode.id}")
                    }
                } catch (e: Exception) {

                }
            }
        }
    }
}
