@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.api.plugins.validators.PathParameterValidator
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.crdt.ShoppingList

/**
 * Payload sent on PUT requests for this node to merge its view of a given shopping list
 */
@Serializable
data class SyncPayload(
    /**
     * The shopping list to merge
     */
    val list: ShoppingList,

    /**
     * Whether this request came from an external client or another node in the cluster.
     */
    val handoff: Boolean = false,
)

/**
 * A response object for a PUT request.
 */
@Serializable
data class SyncResponse(
    /**
     * The merged shopping list
     */
    val list: ShoppingList
)

/**
 * A response for a GET request
 */
@Serializable
data class GetResponse(

    /**
     * The shopping list to return, or *null* if it does not exist.
     */
    val list: ShoppingList?
)

/**
 * Load shopping list API routes
 */
fun Route.loadShoppingListRoutes() {
    put("/echo") {

        @Serializable
        data class EchoPayload(val list: ShoppingList)

        val payload = call.receive<EchoPayload>()

        call.response.header("Test-Endpoint", "true")
        call.respond(HttpStatusCode.OK, payload)
    }

    route("/{listId}") {
        install(PathParameterValidator) {
            pathVariable = "listId"
        }

        get {


            val listId = call.parameters["listId"] as String
            val handoff = call.request.queryParameters["handoff"]

            cluster.getNodeFor(listId)?.let { node ->
                val list = node.get(listId)

                val status: HttpStatusCode = if (list === null) {
                    HttpStatusCode.NotFound
                } else {
                    HttpStatusCode.OK
                }

                call.respond(status, GetResponse(list))
            }
        }

        post {

            val listId = call.parameters["listId"] as String
            val payload = call.receive<SyncPayload>()

            val payloadShoppingList = payload.list
            val handoff = payload.handoff

            cluster.getNodeFor(listId)?.let { node ->
                val shoppingList = node.put(payloadShoppingList.id, payloadShoppingList)

                val responsePayload = SyncResponse(shoppingList)

                call.respond(HttpStatusCode.OK, responsePayload)
                return@post
            }
        }

        loadHealthCheck {
            "Shopping list is healthy"
        }
    }
}
