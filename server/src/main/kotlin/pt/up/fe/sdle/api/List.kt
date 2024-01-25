package pt.up.fe.sdle.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.api.plugins.validators.PathParameterValidator
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.cluster.node.node
import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.logger

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
     * Whether this is a replication-related operation or not
     */
    val replica: Boolean = false,
)

/**
 * A response object for a PUT request.
 */
@Serializable
data class SyncResponse(
    /**
     * The merged shopping list
     */
    val list: ShoppingList,
)

/**
 * A response for a GET request
 */
@Serializable
data class GetResponse(
    /**
     * The shopping list to return, or *null* if it does not exist.
     */
    val list: ShoppingList?,
)

/**
 * Load shopping list API routes
 */
internal fun Route.loadShoppingListRoutes() {
    put("/echo") {
        @Serializable
        data class EchoPayload(val list: ShoppingList)

        val payload = call.receive<EchoPayload>()

        call.response.header("Test-Endpoint", "true")
        call.respond(HttpStatusCode.OK, payload)
    }

    get("/lists") {
    }

    route("/{listId}") {
        install(PathParameterValidator) {
            pathVariable = "listId"
        }

        get {
            val listId = call.parameters["listId"] as String
            val replica = call.request.queryParameters["replica"]?.toBoolean() ?: false

            // if this is a replicated request we want to store locally, regardless of who "holds" the given listId
            (if (replica) node else cluster.getNodeFor(listId))?.let { node ->
                try {
                    val list = node.get(listId, replica)

                    val status: HttpStatusCode =
                        if (list === null) {
                            HttpStatusCode.NotFound
                        } else {
                            HttpStatusCode.OK
                        }

                    call.respond(status, GetResponse(list))
                } catch (exception: Exception) {
                    logger.error("Exception bubbled up to router layer!!", exception)
                    call.respond(HttpStatusCode.NotFound, GetResponse(null))
                }
            }
        }

        post {
            val listId = call.parameters["listId"] as String
            val payload = call.receive<SyncPayload>()

            val payloadShoppingList = payload.list
            val replica = payload.replica

            // if this is a replicated request we want to store locally, regardless of who "holds" the given listId
            (if (replica) node else cluster.getNodeFor(listId))?.let { node ->
                try {
                    val shoppingList = node.put(payloadShoppingList.id, payloadShoppingList, replica)

                    val responsePayload = SyncResponse(shoppingList)

                    call.respond(HttpStatusCode.OK, responsePayload)
                } catch (exception: Exception) {
                    call.respond(HttpStatusCode.BadRequest, SyncResponse(payloadShoppingList))
                }
            }
        }
    }
}
