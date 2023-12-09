package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals

class PNCounterTest {
    @Test
    fun `PNCounter - increment`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc())
    }

    @Test
    fun `PNCounter - increment multiple times`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc())
        assertEquals(2, counter.inc())
        assertEquals(3, counter.inc())
    }

    @Test
    fun `PNCounter - increment by any positive value`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(5, counter.inc(5))
        assertEquals(8, counter.inc(3))
        assertEquals(10, counter.inc(2))
    }

    @Test
    fun `PNCounter - decrement`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(-1, counter.dec())
    }

    @Test
    fun `PNCounter - decrement multiple times`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(-1, counter.dec())
        assertEquals(-2, counter.dec())
        assertEquals(-3, counter.dec())
    }

    @Test
    fun `PNCounter - decrement by any positive value`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(-5, counter.dec(5))
        assertEquals(-8, counter.dec(3))
        assertEquals(-10, counter.dec(2))
    }

    @Test
    fun `PNCounter - increment by a negative value`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(-1, counter.inc(-1))
    }

    @Test
    fun `PNCounter - decrement by a negative value`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.dec(-1))
    }

    @Test
    fun `PNCounter - increment and decrement`() {
        val counter = PNCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc())
        assertEquals(0, counter.dec())
    }

    @Test
    fun `PNCounter - start with a value`() {
        val counter = PNCounter(5)

        assertEquals(5, counter.value)
    }

    @Test
    fun `PNCounter - merge with itself`() {
        val counter = PNCounter()

        counter.inc()
        counter.inc()

        assertEquals(2, counter.merge(counter))
    }

    @Test
    fun `PNCounter - merge with another counter`() {
        val counter1 = PNCounter()
        val counter2 = PNCounter()

        counter1.inc()
        counter1.inc()
        counter2.dec()

        assertEquals(1, counter1.merge(counter2))
    }
}
