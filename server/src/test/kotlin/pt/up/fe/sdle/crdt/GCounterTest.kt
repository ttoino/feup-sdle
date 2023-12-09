package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class GCounterTest {
    @Test
    fun `GCounter - increment`() {
        val counter = GCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc())
    }

    @Test
    fun `GCounter - increment multiple times`() {
        val counter = GCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc())
        assertEquals(2, counter.inc())
        assertEquals(3, counter.inc())
    }

    @Test
    fun `GCounter - increment by any positive value`() {
        val counter = GCounter()

        assertEquals(0, counter.value)
        assertEquals(5, counter.inc(5))
        assertEquals(8, counter.inc(3))
        assertEquals(10, counter.inc(2))
    }

    @Test
    fun `GCounter - increment by a negative value`() {
        val counter = GCounter()

        assertEquals(0, counter.value)
        assertFails { counter.inc(-1) }
    }

    @Test
    fun `GCounter - start with a value`() {
        val counter = GCounter(5)

        assertEquals(5, counter.value)
    }

    @Test
    fun `GCounter - merge with itself`() {
        val counter = GCounter()

        counter.inc()
        counter.inc()

        assertEquals(2, counter.merge(counter))
    }

    @Test
    fun `GCounter - merge with another counter`() {
        val counter1 = GCounter()
        val counter2 = GCounter()

        counter1.inc()
        counter1.inc()
        counter2.inc()

        assertEquals(2, counter1.merge(counter2))
    }
}
