package pt.up.fe.sdle.cluster.node

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageDriver
import pt.up.fe.sdle.storage.StorageKey
import java.util.*

/**
 * Represents the node object running on this instance, which is responsible for storing and retrieving data with a [StorageDriver].
 */
class LocalNode(
    /**
     * The storage driver responsible for storing data on this node
     */
    private var storageDriver: StorageDriver<ShoppingList>,
    address: String = "0.0.0.0",
    id: NodeID = UUID.randomUUID().toString(),
) : Node(address, id) {
    override suspend fun put(
        key: StorageKey,
        data: ShoppingList,
    ): ShoppingList {
        // TODO: implement replication, hinted handoff and request delegation

        val currentShoppingList = this.get(key)

        val mergedShoppingList = currentShoppingList?.merge(data) ?: data

        this.storageDriver.store(key, mergedShoppingList)

        return mergedShoppingList
    }

    override suspend fun get(key: StorageKey): ShoppingList? {
        // TODO: implement replication, hinted handoff and request delegation

        return this.storageDriver.retrieve(key)
    }

    override suspend fun bootstrap() {
        if (bootstrapped) return

        this.cluster.addNode(this@LocalNode)

        val connectIp =
            System.getenv("CONNECT_ADDRESS") ?: run {
                bootstrapped = true
                return@bootstrap
            }

        // Issue a join request to the specified node
        var retries = 0

        @Serializable
        data class JoinPayload(var nodeId: String, var nodeAddress: String)

        // TODO: move this logic elsewhere. This should be in a separate, dedicated software stack
        while (retries++ < MAX_RETRIES) {
            println("Attempting to join cluster of node @ '$connectIp'")

            val response: HttpResponse
            try {
                response =
                    httpClient.post("$connectIp/cluster") {
                        contentType(ContentType.Application.Json)
                        setBody(JoinPayload(this@LocalNode.id, this@LocalNode.address))
                    }
            } catch (_: Exception) {
                // TODO: handle network errors, for now deal with this as if it were a node connecting to itself

                println("Attempting to join cluster of itself")
                return
            }

            if (response.status.isSuccess()) {
                // Cluster join successful on the other side, mark this node as a member of the cluster

                val responsePayload: JoinPayload = response.body<JoinPayload>()

                val otherNode = newWith(responsePayload.nodeId, responsePayload.nodeAddress)

                this.cluster.addNode(otherNode)

                bootstrapped = true

                return
            }

            // Exponential Backoff
            withContext(Dispatchers.IO) {
                Thread.sleep(retries * MIN_TIMEOUT_MS)
            }
        }

        println("Failed to join cluster of node @ '$connectIp'")
    }

    private var bootstrapped = false
}
