package pt.up.fe.sdle

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import pt.up.fe.sdle.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}
