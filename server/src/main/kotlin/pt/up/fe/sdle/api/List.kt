package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import pt.up.fe.sdle.crdt.ShoppingList


/**
 * Load shopping list API routes
 */
fun Route.loadShoppingListRoutes() {
    post("/test") {

        @Serializable
        data class SyncPayload(val list: ShoppingList)
        val payload = call.receive<SyncPayload>()

        val shoppingList = payload.list

        print(shoppingList)

        call.respond(HttpStatusCode.OK, payload)
    }

    route("/{listId}") {

        get {

        }

        post {

        }

        loadHealthCheck {
            "Shopping list is healthy"
        }
    }
}
