package pt.up.fe.sdle.api

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

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
            loadClusterManagementRoutes()
        }
    }
}
