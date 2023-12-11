package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals

private const val ID1 = "a"
private const val ID2 = "b"

class CCounterTest {
    @Test
    fun `CCounter - increment`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc(ID1))
    }

    @Test
    fun `CCounter - increment multiple times`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc(ID1))
        assertEquals(2, counter.inc(ID1))
        assertEquals(3, counter.inc(ID1))
    }

    @Test
    fun `CCounter - increment by any positive value`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(5, counter.inc(ID1, 5))
        assertEquals(8, counter.inc(ID1, 3))
        assertEquals(10, counter.inc(ID1, 2))
    }

    @Test
    fun `CCounter - decrement`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(-1, counter.dec(ID1))
    }

    @Test
    fun `CCounter - decrement multiple times`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(-1, counter.dec(ID1))
        assertEquals(-2, counter.dec(ID1))
        assertEquals(-3, counter.dec(ID1))
    }

    @Test
    fun `CCounter - decrement by any positive value`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(-5, counter.dec(ID1, 5))
        assertEquals(-8, counter.dec(ID1, 3))
        assertEquals(-10, counter.dec(ID1, 2))
    }

    @Test
    fun `CCounter - increment by a negative value`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(-1, counter.inc(ID1, -1))
    }

    @Test
    fun `CCounter - decrement by a negative value`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.dec(ID1, -1))
    }

    @Test
    fun `CCounter - increment and decrement`() {
        val counter = CCounter()

        assertEquals(0, counter.value)
        assertEquals(1, counter.inc(ID1))
        assertEquals(0, counter.dec(ID1))
    }

    @Test
    fun `CCounter - merge with itself`() {
        val counter = CCounter()

        counter.inc(ID1)
        counter.inc(ID1)

        assertEquals(2, counter.merge(counter))
    }

    @Test
    fun `CCounter - merge with another counter`() {
        val counter1 = CCounter()
        val counter2 = CCounter()

        counter1.inc(ID1)
        counter2.inc(ID2)

        assertEquals(2, counter1.merge(counter2))
    }
}
