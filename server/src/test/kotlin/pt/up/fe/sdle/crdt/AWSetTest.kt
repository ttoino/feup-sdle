package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals

private const val ID1 = "a"
private const val ID2 = "b"

internal class AWSetTest {
    @Test
    fun `AWSet - add element`() {
        val set = AWSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(ID1, 1))
    }

    @Test
    fun `AWSet - add elements`() {
        val set = AWSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(ID1, 1))
        assertEquals(
            setOf(
                1,
                2,
            ),
            set.add(ID1, 2),
        )
        assertEquals(
            setOf(1, 2, 3),
            set.add(ID1, 3),
        )
    }

    @Test
    fun `AWSet - add element multiple times`() {
        val set = AWSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(ID1, 1))
        assertEquals(setOf(1), set.add(ID1, 1))
        assertEquals(setOf(1), set.add(ID1, 1))
    }

    @Test
    fun `AWSet - remove element`() {
        val set = AWSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(ID1, 1))
        assertEquals(set.remove(1), setOf())
    }

    @Test
    fun `AWSet - remove elements`() {
        val set = AWSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(ID1, 1))
        assertEquals(
            setOf(
                1,
                2,
            ),
            set.add(ID1, 2),
        )
        assertEquals(
            setOf(
                1,
                2,
                3,
            ),
            set.add(ID1, 3),
        )
        assertEquals(
            setOf(
                2,
                3,
            ),
            set.remove(1),
        )
        assertEquals(
            setOf(
                3,
            ),
            set.remove(2),
        )
        assertEquals(setOf(), set.remove(3))
    }

    @Test
    fun `AWSet - remove element multiple times`() {
        val set = AWSet<Int>()

        assertEquals(setOf(), set.value)
        assertEquals(setOf(1), set.add(ID1, 1))
        assertEquals(setOf(), set.remove(1))
        assertEquals(setOf(), set.remove(1))
        assertEquals(setOf(), set.remove(1))
    }

    @Test
    fun `AWSet - merge with itself`() {
        val set = AWSet<Int>()

        set.add(ID1, 1)
        set.add(ID1, 2)

        assertEquals(
            setOf(
                1,
                2,
            ),
            set.merge(set),
        )
    }

    @Test
    fun `AWSet - merge with another set`() {
        val set1 = AWSet<Int>()
        val set2 = AWSet<Int>()

        set1.add(ID1, 1)
        set1.add(ID1, 2)

        set2.add(ID2, 3)

        assertEquals(
            setOf(
                1,
                2,
                3,
            ),
            set1.merge(set2),
        )
    }

    @Test
    fun `should be able to merge with another set with conflicting adds`() {
        val set1 = AWSet<Int>()
        val set2 = AWSet<Int>()

        set1.add(ID1, 1)
        set1.add(ID1, 1)

        assertEquals(
            setOf(
                1,
            ),
            set1.merge(set2),
        )
    }

    @Test
    fun `should be able to merge with another set with conflicting removes`() {
        val set1 = AWSet<Int>()
        val set2 = AWSet<Int>()

        set1.add(ID1, 1)
        set1.remove(1)
        set2.remove(1)

        assertEquals(setOf(), set1.merge(set2))
    }

    @Test
    fun `should be able to merge with another set with conflicting adds and removes`() {
        val set1 = AWSet<Int>()
        val set2 = AWSet<Int>()

        set1.add(ID1, 1)
        set1.remove(1)
        set2.add(ID2, 1)

        assertEquals(setOf(1), set1.merge(set2))
    }
}
