package pt.up.fe.sdle.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 *
 */
fun Application.configureRouting() {
    routing {
        loadHealthCheck()
    }
}
