package pt.up.fe.sdle.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Registers a "health check" endpoint at the path this is called.
 */
fun Route.loadHealthCheck() {
    get {
        call.respond(mapOf("online" to true))
    }
}
