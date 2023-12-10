@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.api.plugins.validators.PathParameterValidator
import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.cluster.cluster
import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.crdt.ShoppingList

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
            @Serializable
            data class GetPayload(val list: ShoppingList?)

            val listId = call.parameters["listId"] as String

            cluster.getNodeFor(listId)?.let { node ->
                val list = node.get(listId)

                val status: HttpStatusCode =
                    if (list === null) {
                        HttpStatusCode.NotFound
                    } else {
                        HttpStatusCode.OK
                    }

                call.respond(status, GetPayload(list))
            }
        }

        post {
            @Serializable
            data class SyncPayload(val list: ShoppingList)

            val listId = call.parameters["listId"] as String
            val payload = call.receive<SyncPayload>()

            val payloadShoppingList = payload.list

            cluster.getNodeFor(listId)?.let { node ->
                val shoppingList = node.put(payloadShoppingList.id, payloadShoppingList)

                val responsePayload = SyncPayload(shoppingList)

                call.respond(HttpStatusCode.OK, responsePayload)
            }
        }

        loadHealthCheck {
            "Shopping list is healthy"
        }
    }
}
