@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.server.routing.*

/**
 * Registers a "health check" endpoint at the path this is called.
 */
fun Route.loadClusterManagementRoutes() {
    route("/{clusterId}") {
        loadHealthCheck {
            "caralhoooo"
        }
    }
}
