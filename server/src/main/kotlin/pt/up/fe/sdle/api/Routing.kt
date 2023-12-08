package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 *
 */
fun Application.configureRouting() {
    routing {
        loadHealthCheck()

        route("/list") {
            loadHealthCheck {
                "list"
            }

            loadShoppingListRoutes()
        }

        route("/cluster") {
            loadHealthCheck {
                "cluster"
            }

            loadClusterManagementRoutes()
        }
    }
}
