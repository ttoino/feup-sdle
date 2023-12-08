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
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json
import org.slf4j.event.*
import pt.up.fe.sdle.api.configureRouting

/**
 *
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 *
 */
fun Application.module() {
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
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            },
        )
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    configureRouting()
}
