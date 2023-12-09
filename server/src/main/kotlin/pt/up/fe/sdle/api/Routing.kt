@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.server.application.*
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
