package pt.up.fe.sdle.cluster.node.services.bootstrap

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.api.ClusterJoinResponse
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.logger


/**
 * Bootstrap service that connects a node to an existing cluster if configured to tho so
 */
class NodeBootstrapService(private val node: Node, private val httpClient: HttpClient) : BootstrapService {

    private var bootstrapped = false

    override suspend fun bootstrap() {
        if (bootstrapped) return

        val connectIp =
            System.getenv("CONNECT_ADDRESS") ?: run {
                bootstrapped = true
                return
            }

        // Issue a join request to the specified node
        var retries = 0

        @Serializable
        data class JoinPayload(var nodeId: String, var nodeAddress: String)

        while (retries++ < MAX_RETRIES) {
            logger.info("Attempting to join cluster of node @ '$connectIp'")

            val response: HttpResponse
            try {
                response =
                    httpClient.post("$connectIp/cluster") {
                        contentType(ContentType.Application.Json)
                        setBody(JoinPayload(node.id, node.address))
                    }
            } catch (_: Exception) {
                // TODO: handle network errors, for now deal with this as if it were a node connecting to itself

                logger.warn("Attempting to join cluster of itself")
                return
            }

            if (response.status.isSuccess()) {
                // Cluster join successful on the other side, mark this node as a member of the cluster

                val responsePayload = response.body<ClusterJoinResponse>()

                val otherNode = responsePayload.node
                val clusterView = responsePayload.nodes

                node.cluster.updateNodeStatus(otherNode)
                node.cluster.updateNodeStatuses(clusterView)

                bootstrapped = true

                logger.info("Joined cluster of node @ '$connectIp'")

                return
            }

            // Exponential Backoff
            withContext(Dispatchers.IO) {
                Thread.sleep(retries * MIN_TIMEOUT_MS)
            }
        }

        logger.error("Failed to join cluster of node @ '$connectIp'")
    }

    private companion object {
        /**
         * The minimum amount of time, in milliseconds, to wait before attempting to reconnect to the given cluster IP.
         *
         * This value is used in conjunction with an integer that goes from 0 to [MAX_RETRIES] to provide an exponential backoff mechanism.
         */
        val MIN_TIMEOUT_MS: Long = System.getenv("CLUSTER_CONNECT_MIN_TIMEOUT")?.toLong() ?: 500

        /**
         * The maximum number of retries to try before giving up on connecting to a cluster.
         */
        val MAX_RETRIES = System.getenv("CLUSTER_CONNECT_MAX_RETRIES")?.toInt() ?: 3
    }
}
