@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

/**
 * [HealthCheck] structure for responding to health-check requests.
 */
@Serializable
internal data class HealthCheck(
    /**
     * The status of the endpoint this healthcheck is installed on. Defaults to true.
     */
    val online: Boolean,
    /**
     * The path where this health-check was installed.
     */
    val path: String,
    /**
     * Optional message passed in on the Healthcheck.
     */
    val message: String?,
)

/**
 * Registers a "health check" endpoint at the path this is called.
 *
 * Sends a custom messaged on the payload defined by the [messageProducer] implementation.
 *
 * @param [messageProducer] a closure that receives the current [ApplicationCall] and returns a custom string to be appended on the payload.
 */
internal fun Route.loadHealthCheck(messageProducer: (ApplicationCall) -> String) {
    get {
        val payload = HealthCheck(online = true, path = call.request.path(), message = messageProducer(call))

        call.application.environment.log.info("Responding to health check with custom message: \"%s\"".format(payload))

        call.respond(HttpStatusCode.OK, payload)
    }
}

/**
 * Registers a "health check" endpoint at the path this is called.
 */
internal fun Route.loadHealthCheck() {
    get {
        call.respond(HttpStatusCode.OK, HealthCheck(online = true, path = call.request.path(), message = null))
    }
}
