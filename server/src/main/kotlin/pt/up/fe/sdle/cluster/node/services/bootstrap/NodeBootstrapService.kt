package pt.up.fe.sdle.cluster.node.services.bootstrap

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.up.fe.sdle.api.ClusterJoinRequest
import pt.up.fe.sdle.api.ClusterJoinResponse
import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.logger

/**
 * Bootstrap service that connects a node to an existing cluster if configured to tho so
 */
class NodeBootstrapService(private val node: Node, private val httpClient: HttpClient) : BootstrapService {
    private var bootstrapped = false

    override suspend fun bootstrap() {
        if (bootstrapped) return

        val primaryConnectIp =
            Cluster.Config.Bootstrap.CONNECT_ADDRESS ?: run {
                bootstrapped = true
                return
            }

        val connectIps = listOf(primaryConnectIp) + Cluster.Config.Bootstrap.CONNECT_ADDRESSES

        ips@ for (connectIp in connectIps) {
            // Issue a join request to the specified node
            var retries = 0
            while (retries < MAX_RETRIES) {
                logger.info("Attempting to join cluster @ $connectIp: attempt ${++retries}")

                val response: HttpResponse
                try {
                    response =
                        httpClient.post("http://$connectIp/cluster") {
                            contentType(ContentType.Application.Json)
                            setBody(ClusterJoinRequest(node.id))
                        }
                } catch (_: Exception) {
                    // TODO: handle network errors, for now deal with this as if it were a node connecting to itself

                    logger.warn("Attempting to join cluster of itself")
                    continue@ips
                }

                if (response.status.isSuccess()) {
                    // Cluster join successful on the other side, mark this node as a member of the cluster

                    val responsePayload = response.body<ClusterJoinResponse>()

                    val otherNode = responsePayload.node
                    val clusterView = responsePayload.nodes

                    // We can do this since we guarantee that, to reach that node, we had to make a request to 'connectIp', just recycle that.
                    node.cluster.updateNodeStatus(otherNode.copy(nodeAddress = connectIp))
                    node.cluster.updateNodeStatuses(clusterView)

                    bootstrapped = true

                    logger.info("Joined cluster @ '$connectIp'")

                    continue@ips
                }

                // Exponential Backoff
                withContext(Dispatchers.IO) {
                    Thread.sleep(retries * MIN_TIMEOUT_MS)
                }
            }

            logger.error("Failed to join cluster @ '$connectIp'")
        }
    }

    private companion object {
        /**
         * The minimum amount of time, in milliseconds, to wait before attempting to reconnect to the given cluster IP.
         *
         * This value is used in conjunction with an integer that goes from 0 to [MAX_RETRIES] to provide an exponential backoff mechanism.
         */
        val MIN_TIMEOUT_MS: Long = Cluster.Config.Bootstrap.MIN_TIMEOUT_MS

        /**
         * The maximum number of retries to try before giving up on connecting to a cluster.
         */
        val MAX_RETRIES = Cluster.Config.Bootstrap.MAX_RETRIES
    }
}
