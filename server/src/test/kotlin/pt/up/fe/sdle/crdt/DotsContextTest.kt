package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val ID1 = "a"
private const val ID2 = "b"

class DotsContextTest {
    @Test
    fun `DotsContext - get next dot`() {
        val dots = DotsContext()

        assertEquals(Dot(ID1, 1), dots.next(ID1))
        assertEquals(Dot(ID1, 2), dots.next(ID1))
        assertEquals(Dot(ID1, 3), dots.next(ID1))
    }

    @Test
    fun `DotsContext - get next dot for multiple ids`() {
        val dots = DotsContext()

        assertEquals(Dot(ID1, 1), dots.next(ID1))
        assertEquals(Dot(ID2, 1), dots.next(ID2))
        assertEquals(Dot(ID1, 2), dots.next(ID1))
        assertEquals(Dot(ID2, 2), dots.next(ID2))
        assertEquals(Dot(ID1, 3), dots.next(ID1))
        assertEquals(Dot(ID2, 3), dots.next(ID2))
    }

    @Test
    fun `DotsContext - get max dot`() {
        val dots = DotsContext()

        assertEquals(0, dots.max(ID1))
        assertEquals(Dot(ID1, 1), dots.next(ID1))
        assertEquals(1, dots.max(ID1))
        assertEquals(Dot(ID1, 2), dots.next(ID1))
        assertEquals(2, dots.max(ID1))
        assertEquals(Dot(ID1, 3), dots.next(ID1))
        assertEquals(3, dots.max(ID1))
    }

    @Test
    fun `DotsContext - get max dot for multiple ids`() {
        val dots = DotsContext()

        assertEquals(0, dots.max(ID1))
        assertEquals(Dot(ID1, 1), dots.next(ID1))
        assertEquals(1, dots.max(ID1))
        assertEquals(Dot(ID2, 1), dots.next(ID2))
        assertEquals(1, dots.max(ID2))
        assertEquals(Dot(ID1, 2), dots.next(ID1))
        assertEquals(2, dots.max(ID1))
        assertEquals(Dot(ID2, 2), dots.next(ID2))
        assertEquals(2, dots.max(ID2))
        assertEquals(Dot(ID1, 3), dots.next(ID1))
        assertEquals(3, dots.max(ID1))
        assertEquals(Dot(ID2, 3), dots.next(ID2))
        assertEquals(3, dots.max(ID2))
    }

    @Test
    fun `DotsContext - has dot`() {
        val dots = DotsContext()

        assertFalse(dots.has(ID1, 1))
        assertEquals(Dot(ID1, 1), dots.next(ID1))
        assertTrue(dots.has(ID1, 1))
        assertFalse(dots.has(ID1, 2))
        assertEquals(Dot(ID1, 2), dots.next(ID1))
        assertTrue(dots.has(ID1, 2))
        assertFalse(dots.has(ID1, 3))
        assertEquals(Dot(ID1, 3), dots.next(ID1))
        assertTrue(dots.has(ID1, 3))
    }

    @Test
    fun `DotsContext - has dot for multiple ids`() {
        val dots = DotsContext()

        assertFalse(dots.has(ID1, 1))
        assertEquals(Dot(ID1, 1), dots.next(ID1))
        assertTrue(dots.has(ID1, 1))
        assertFalse(dots.has(ID2, 1))
        assertEquals(Dot(ID2, 1), dots.next(ID2))
        assertTrue(dots.has(ID2, 1))
        assertFalse(dots.has(ID1, 2))
        assertEquals(Dot(ID1, 2), dots.next(ID1))
        assertTrue(dots.has(ID1, 2))
        assertFalse(dots.has(ID2, 2))
        assertEquals(Dot(ID2, 2), dots.next(ID2))
        assertTrue(dots.has(ID2, 2))
        assertFalse(dots.has(ID1, 3))
        assertEquals(Dot(ID1, 3), dots.next(ID1))
        assertTrue(dots.has(ID1, 3))
        assertFalse(dots.has(ID2, 3))
        assertEquals(Dot(ID2, 3), dots.next(ID2))
        assertTrue(dots.has(ID2, 3))
    }

    @Test
    fun `DotsContext - merge with itself`() {
        val dots = DotsContext()

        dots.next(ID1)
        dots.next(ID1)
        dots.next(ID1)

        assertEquals(dots, dots.merge(dots))
    }

    @Test
    fun `DotsContext - merge with another context`() {
        val dots1 = DotsContext()
        val dots2 = DotsContext()

        dots1.next(ID1)
        dots1.next(ID1)
        dots2.next(ID1)
        dots2.next(ID1)
        dots2.next(ID1)

        assertEquals(DotsContext(mutableMapOf(ID1 to 3)), dots1.merge(dots2))
    }

    @Test
    fun `DotsContext - merge with another context for multiple ids`() {
        val dots1 = DotsContext()
        val dots2 = DotsContext()

        dots1.next(ID1)
        dots1.next(ID1)
        dots2.next(ID2)
        dots2.next(ID2)
        dots2.next(ID2)

        assertEquals(DotsContext(mutableMapOf(ID1 to 2, ID2 to 3)), dots1.merge(dots2))
    }
}
