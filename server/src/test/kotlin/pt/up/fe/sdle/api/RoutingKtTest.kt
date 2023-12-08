package pt.up.fe.sdle.api

import io.ktor.client.request.*
import io.ktor.server.testing.*
import pt.up.fe.sdle.module
import kotlin.test.Test
import kotlin.test.asserter

class RoutingKtTest {
    @Test
    fun testGet() =
        testApplication {
            application {
                module()
            }
            client.get("/").apply {
                asserter.assertTrue(null, true)
            }
        }
}
