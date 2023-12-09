package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals

class GSetTest {
    @Test
    fun `GSet - add an element`() {
        val set = GSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(1))
    }

    @Test
    fun `GSet - add multiple elements`() {
        val set = GSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(1))
        assertEquals(setOf(1, 2), set.add(2))
        assertEquals(setOf(1, 2, 3), set.add(3))
    }

    @Test
    fun `GSet - add an element multiple times`() {
        val set = GSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(1))
        assertEquals(setOf(1), set.add(1))
        assertEquals(setOf(1), set.add(1))
    }

    @Test
    fun `GSet - merge with itself`() {
        val set = GSet<Int>()

        set.add(1)
        set.add(2)

        assertEquals(setOf(1, 2), set.merge(set))
    }

    @Test
    fun `GSet - merge with another set`() {
        val set1 = GSet<Int>()
        val set2 = GSet<Int>()

        set1.add(1)
        set1.add(2)
        set2.add(3)

        assertEquals(setOf(1, 2, 3), set1.merge(set2))
    }
}
