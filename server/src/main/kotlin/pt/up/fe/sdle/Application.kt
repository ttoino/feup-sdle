@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.event.Level
import pt.up.fe.sdle.api.configureRouting
import pt.up.fe.sdle.cluster.node.LocalNode
import pt.up.fe.sdle.cluster.node.Node

/**
 *
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Application logger
 */
lateinit var logger: Logger

/**
 *
 */
fun Application.module() {
    logger = environment.log
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(AutoHeadResponse)
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
    install(CORS) {
        anyHost()
        allowHeadersPrefixed("")
//        allowHost("0.0.0.0:5173")
        allowHeader(HttpHeaders.ContentType)
//        allowHeader(HttpHeaders.Accept)
    }

    configureRouting()

    val node = Node.newWithAddress("${environment.config.host}:${environment.config.port}")
    log.info("Bootstrapping ${if (node is LocalNode) "local" else "remote"} node with id: ${node.id}")
    launch {
        node.bootstrap()
    }
}
