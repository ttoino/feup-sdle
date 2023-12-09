package pt.up.fe.sdle.api.plugins.validators

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.response.respond

/**
 * Configuration used by the [PathParameterValidator] plugin.
 */
class PathParameterPluginConfig {
    /**
     * The variable under validation.
     * 
     * Defaults to "id".
     */
    var pathVariable: String = "id"
}

/**
 * Plugin that performs validation against a path parameter.
 *
 * For now, it only validates that the parameter exists and is not null/empty
 */
val PathParameterValidator =
    createRouteScopedPlugin("PathParameterValidator", createConfiguration = ::PathParameterPluginConfig) {
        val pathVariable = pluginConfig.pathVariable

        onCallReceive { call ->
            val pathVariableValue = call.parameters[pathVariable]

            if (pathVariableValue === null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Field must be present", "path" to pathVariable))
            }

            return@onCallReceive
        }
    }
