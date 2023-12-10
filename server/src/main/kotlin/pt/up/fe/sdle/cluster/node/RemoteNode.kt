@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import pt.up.fe.sdle.api.GetResponse
import pt.up.fe.sdle.api.SyncPayload
import pt.up.fe.sdle.api.SyncResponse
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.logger
import pt.up.fe.sdle.storage.StorageKey
import java.lang.Exception
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
    override suspend fun bootstrap() {
        logger.warn("Bootstrapping RemoteNode does nothing")
    }

    override suspend fun put(
        key: StorageKey,
        data: ShoppingList,
    ): ShoppingList {

        val payload = SyncPayload(data, true)

        logger.info("Received delegated PUT call for node with id $id at address $address")

        val response: HttpResponse

        try {
            response = httpClient.post("http://$address/list/${data.id}") {
                    contentType(ContentType.Application.Json)
                    setBody(payload)
                }
        } catch (e: Exception) {
            // TODO: "Network error"

            logger.error("Unknown network error: $e")

            return data
        }

        if (!response.status.isSuccess()) {
            TODO("Handle request failure")
        }

        val responseData = response.body<SyncResponse>()

        return responseData.list
    }

    override suspend fun get(key: StorageKey): ShoppingList? {

        logger.info("Received delegated GET call for node with id $id at address $address")

        val response: HttpResponse

        try {
            // FIXME: this is a bit weird because we do not need to know that the storage key is the list id
            response = httpClient.get("http://$address/list/${key}") {
                accept(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            // TODO: "Network error"

            logger.error("Unknown network error: $e")

            return null
        }

        if (!response.status.isSuccess()) {
            TODO("Handle request failure")
        }

        val responseData = response.body<GetResponse>()

        return responseData.list
    }
}
