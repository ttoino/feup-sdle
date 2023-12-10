package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals

private const val ID1 = "a"
private const val ID2 = "b"

class MVRegisterTest {
    @Test
    fun `MVRegister - assign a value`() {
        val register = MVRegister<Int>()

        assertEquals(setOf(), register.value)
        assertEquals(setOf(1), register.assign(ID1, 1))
    }

    @Test
    fun `MVRegister - assign a value multiple times`() {
        val register = MVRegister<Int>()

        assertEquals(setOf(), register.value)
        assertEquals(setOf(1), register.assign(ID1, 1))
        assertEquals(setOf(1), register.assign(ID1, 1))
        assertEquals(setOf(1), register.assign(ID1, 1))
    }

    @Test
    fun `MVRegister - assign a value to a different id`() {
        val register = MVRegister<Int>()

        assertEquals(setOf(), register.value)
        assertEquals(setOf(1), register.assign(ID1, 1))
        assertEquals(setOf(2), register.assign(ID2, 2))
    }

    @Test
    fun `MVRegister - assign a value to a different id multiple times`() {
        val register = MVRegister<Int>()

        assertEquals(setOf(), register.value)
        assertEquals(setOf(1), register.assign(ID1, 1))
        assertEquals(setOf(2), register.assign(ID2, 2))
        assertEquals(setOf(1), register.assign(ID1, 1))
        assertEquals(setOf(2), register.assign(ID2, 2))
        assertEquals(setOf(1), register.assign(ID1, 1))
        assertEquals(setOf(2), register.assign(ID2, 2))
    }

    @Test
    fun `MVRegister - merge with itself`() {
        val register = MVRegister<Int>()

        register.assign(ID1, 1)

        assertEquals(setOf(1), register.merge(register))
    }

    @Test
    fun `MVRegister - merge with another register`() {
        val register1 = MVRegister<Int>()
        val register2 = MVRegister<Int>()

        register1.assign(ID1, 1)
        register2.merge(register1)
        register2.assign(ID2, 2)

        assertEquals(setOf(2), register1.merge(register2))
    }

    @Test
    fun `MVRegister - merge with another register with conflicting values`() {
        val register1 = MVRegister<Int>()
        val register2 = MVRegister<Int>()

        register1.assign(ID1, 1)
        register2.assign(ID2, 2)

        assertEquals(setOf(1, 2), register1.merge(register2))
    }
}
