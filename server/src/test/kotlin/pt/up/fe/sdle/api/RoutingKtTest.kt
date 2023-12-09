package pt.up.fe.sdle.api

import io.ktor.client.request.get
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.asserter

class RoutingKtTest {
    @Test
    fun testGet() =
        testApplication {
            client.get("/").apply {
                asserter.assertTrue(null, true)
            }
        }
}
