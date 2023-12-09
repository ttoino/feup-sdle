package pt.up.fe.sdle.crdt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

private const val ID1 = "a"
private const val ID2 = "b"

internal class AWMapTest {
    @Test
    fun `AWMap - set AWSet element`() {
        val map = AWMap<String, AWSet<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to AWSet()), map.set(ID1, "test", AWSet()))
    }

    @Test
    fun `AWMap - set CCounter element`() {
        val map = AWMap<String, CCounter>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to CCounter()), map.set(ID1, "test", CCounter()))
    }

    @Test
    fun `AWMap - set MVRegister element`() {
        val map = AWMap<String, MVRegister<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to MVRegister<Int>()), map.set(ID1, "test", MVRegister<Int>()))
    }

    @Test
    fun `AWMap - set AWSet elements`() {
        val map = AWMap<String, AWSet<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test1" to AWSet()), map.set(ID1, "test1", AWSet()))
        assertEquals(
            mapOf(
                "test1" to AWSet(),
                "test2" to AWSet(),
            ),
            map.set(ID1, "test2", AWSet()),
        )
        assertEquals(
            mapOf(
                "test1" to AWSet(),
                "test2" to AWSet(),
                "test3" to AWSet(),
            ),
            map.set(ID1, "test3", AWSet()),
        )
    }

    @Test
    fun `AWMap - set CCounter elements`() {
        val map = AWMap<String, CCounter>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test1" to CCounter()), map.set(ID1, "test1", CCounter()))
        assertEquals(
            mapOf(
                "test1" to CCounter(),
                "test2" to CCounter(),
            ),
            map.set(ID1, "test2", CCounter()),
        )
        assertEquals(
            mapOf(
                "test1" to CCounter(),
                "test2" to CCounter(),
                "test3" to CCounter(),
            ),
            map.set(ID1, "test3", CCounter()),
        )
    }

    @Test
    fun `AWMap - set MVRegister elements`() {
        val map = AWMap<String, MVRegister<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test1" to MVRegister<Int>()), map.set(ID1, "test1", MVRegister()))
        assertEquals(
            mapOf(
                "test1" to MVRegister(),
                "test2" to MVRegister(),
            ),
            map.set(ID1, "test2", MVRegister()),
        )
        assertEquals(
            mapOf(
                "test1" to MVRegister(),
                "test2" to MVRegister(),
                "test3" to MVRegister(),
            ),
            map.set(ID1, "test3", MVRegister()),
        )
    }

    @Test
    fun `AWMap - set AWSet element multiple times`() {
        val map = AWMap<String, AWSet<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to AWSet()), map.set(ID1, "test", AWSet()))
        assertEquals(mapOf("test" to AWSet()), map.set(ID1, "test", AWSet()))
        assertEquals(mapOf("test" to AWSet()), map.set(ID1, "test", AWSet()))
    }

    @Test
    fun `AWMap - set CCounter element multiple times`() {
        val map = AWMap<String, CCounter>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to CCounter()), map.set(ID1, "test", CCounter()))
        assertEquals(mapOf("test" to CCounter()), map.set(ID1, "test", CCounter()))
        assertEquals(mapOf("test" to CCounter()), map.set(ID1, "test", CCounter()))
    }

    @Test
    fun `AWMap - set MVRegister element multiple times`() {
        val map = AWMap<String, MVRegister<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to MVRegister()), map.set(ID1, "test", MVRegister()))
        assertEquals(mapOf("test" to MVRegister()), map.set(ID1, "test", MVRegister()))
        assertEquals(mapOf("test" to MVRegister()), map.set(ID1, "test", MVRegister()))
    }

    @Test
    fun `AWMap - remove AWSet element`() {
        val map = AWMap<String, AWSet<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to AWSet()), map.set(ID1, "test", AWSet()))
        assertEquals(map.remove("test"), mapOf())
    }

    @Test
    fun `AWMap - remove CCounter element`() {
        val map = AWMap<String, CCounter>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to CCounter()), map.set(ID1, "test", CCounter()))
        assertEquals(mapOf(), map.remove("test"))
    }

    @Test
    fun `AWMap - remove MVRegister element`() {
        val map = AWMap<String, MVRegister<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to MVRegister()), map.set(ID1, "test", MVRegister()))
        assertEquals(mapOf(), map.remove("test"))
    }

    @Test
    fun `AWMap - remove AWSet elements`() {
        val map = AWMap<String, AWSet<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test1" to AWSet()), map.set(ID1, "test1", AWSet()))
        assertEquals(
            mapOf(
                "test1" to AWSet(),
                "test2" to AWSet(),
            ),
            map.set(ID1, "test2", AWSet()),
        )
        assertEquals(
            mapOf(
                "test1" to AWSet(),
                "test2" to AWSet(),
                "test3" to AWSet(),
            ),
            map.set(ID1, "test3", AWSet()),
        )
        assertEquals(
            mapOf(
                "test2" to AWSet(),
                "test3" to AWSet(),
            ),
            map.remove("test1"),
        )
        assertEquals(
            mapOf(
                "test3" to AWSet(),
            ),
            map.remove("test2"),
        )
        assertEquals(mapOf(), map.remove("test3"))
    }

    @Test
    fun `AWMap - remove CCounter elements`() {
        val map = AWMap<String, CCounter>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test1" to CCounter()), map.set(ID1, "test1", CCounter()))
        assertEquals(
            mapOf(
                "test1" to CCounter(),
                "test2" to CCounter(),
            ),
            map.set(ID1, "test2", CCounter()),
        )
        assertEquals(
            mapOf(
                "test1" to CCounter(),
                "test2" to CCounter(),
                "test3" to CCounter(),
            ),
            map.set(ID1, "test3", CCounter()),
        )
        assertEquals(
            mapOf(
                "test2" to CCounter(),
                "test3" to CCounter(),
            ),
            map.remove("test1"),
        )
        assertEquals(
            mapOf(
                "test3" to CCounter(),
            ),
            map.remove("test2"),
        )
        assertEquals(mapOf(), map.remove("test3"))
    }

    @Test
    fun `AWMap - remove MVRegister elements`() {
        val map = AWMap<String, MVRegister<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test1" to MVRegister()), map.set(ID1, "test1", MVRegister()))
        assertEquals(
            mapOf(
                "test1" to MVRegister(),
                "test2" to MVRegister(),
            ),
            map.set(ID1, "test2", MVRegister()),
        )
        assertEquals(
            mapOf(
                "test1" to MVRegister(),
                "test2" to MVRegister(),
                "test3" to MVRegister(),
            ),
            map.set(ID1, "test3", MVRegister()),
        )
        assertEquals(
            mapOf(
                "test2" to MVRegister(),
                "test3" to MVRegister(),
            ),
            map.remove("test1"),
        )
        assertEquals(
            mapOf(
                "test3" to MVRegister(),
            ),
            map.remove("test2"),
        )
        assertEquals(mapOf(), map.remove("test3"))
    }

    @Test
    fun `AWMap - remove AWSet element multiple times`() {
        val map = AWMap<String, AWSet<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to AWSet()), map.set(ID1, "test", AWSet()))
        assertEquals(mapOf(), map.remove("test"))
        assertEquals(mapOf(), map.remove("test"))
        assertEquals(mapOf(), map.remove("test"))
    }

    @Test
    fun `AWMap - remove CCounter element multiple times`() {
        val map = AWMap<String, CCounter>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to CCounter()), map.set(ID1, "test", CCounter()))
        assertEquals(mapOf(), map.remove("test"))
        assertEquals(mapOf(), map.remove("test"))
        assertEquals(mapOf(), map.remove("test"))
    }

    @Test
    fun `AWMap - remove MVRegister element multiple times`() {
        val map = AWMap<String, MVRegister<Int>>()

        assertEquals(mapOf(), map.value)
        assertEquals(mapOf("test" to MVRegister()), map.set(ID1, "test", MVRegister()))
        assertEquals(mapOf(), map.remove("test"))
        assertEquals(mapOf(), map.remove("test"))
        assertEquals(mapOf(), map.remove("test"))
    }

    @Test
    fun `AWMap - merge with itself with AWSet elements`() {
        val map = AWMap<String, AWSet<Int>>()

        map.set(ID1, "test1", AWSet())
        map.set(ID1, "test2", AWSet())

        assertEquals(
            mapOf(
                "test1" to AWSet(),
                "test2" to AWSet(),
            ),
            map.merge(map),
        )
    }

    @Test
    fun `AWMap - merge with itself with CCounter elements`() {
        val map = AWMap<String, CCounter>()

        map.set(ID1, "test1", CCounter())
        map.set(ID1, "test2", CCounter())

        assertEquals(
            mapOf(
                "test1" to CCounter(),
                "test2" to CCounter(),
            ),
            map.merge(map),
        )
    }

    @Test
    fun `AWMap - merge with itself with MVRegister elements`() {
        val map = AWMap<String, MVRegister<Int>>()

        map.set(ID1, "test1", MVRegister())
        map.set(ID1, "test2", MVRegister())

        assertEquals(
            mapOf(
                "test1" to MVRegister(),
                "test2" to MVRegister(),
            ),
            map.merge(map),
        )
    }

    @Test
    fun `AWMap - merge with another map with AWSet elements`() {
        val map1 = AWMap<String, AWSet<Int>>()
        val map2 = AWMap<String, AWSet<Int>>()

        map1.set(ID1, "test1", AWSet())
        map1.set(ID1, "test2", AWSet())

        map2.set(ID2, "test3", AWSet())

        assertEquals(
            mapOf(
                "test1" to AWSet(),
                "test2" to AWSet(),
                "test3" to AWSet(),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with CCounter elements`() {
        val map1 = AWMap<String, CCounter>()
        val map2 = AWMap<String, CCounter>()

        map1.set(ID1, "test1", CCounter())
        map1.set(ID1, "test2", CCounter())

        map2.set(ID2, "test3", CCounter())

        assertEquals(
            mapOf(
                "test1" to CCounter(),
                "test2" to CCounter(),
                "test3" to CCounter(),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with MVRegister elements`() {
        val map1 = AWMap<String, MVRegister<Int>>()
        val map2 = AWMap<String, MVRegister<Int>>()

        map1.set(ID1, "test1", MVRegister())
        map1.set(ID1, "test2", MVRegister())

        map2.set(ID2, "test3", MVRegister())

        assertEquals(
            mapOf(
                "test1" to MVRegister(),
                "test2" to MVRegister(),
                "test3" to MVRegister(),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with AWSet elements with conflicting adds`() {
        val map1 = AWMap<String, AWSet<Int>>()
        val map2 = AWMap<String, AWSet<Int>>()

        map1.set(ID1, "test", AWSet())
        map1.set(ID1, "test", AWSet())

        assertEquals(
            mapOf(
                "test" to AWSet(),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with CCounter elements with conflicting adds`() {
        val map1 = AWMap<String, CCounter>()
        val map2 = AWMap<String, CCounter>()

        map1.set(ID1, "test", CCounter())
        map1.set(ID1, "test", CCounter())

        assertEquals(
            mapOf(
                "test" to CCounter(),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with MVRegister elements with conflicting adds`() {
        val map1 = AWMap<String, MVRegister<Int>>()
        val map2 = AWMap<String, MVRegister<Int>>()

        map1.set(ID1, "test", MVRegister())
        map1.set(ID1, "test", MVRegister())

        assertEquals(
            mapOf(
                "test" to MVRegister(),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with AWSet elements with conflicting removes`() {
        val map1 = AWMap<String, AWSet<Int>>()
        val map2 = AWMap<String, AWSet<Int>>()

        map1.set(ID1, "test", AWSet())
        map1.remove("test")
        map2.remove("test")

        assertEquals(mapOf(), map1.merge(map2))
    }

    @Test
    fun `AWMap - merge with another map with CCounter elements with conflicting removes`() {
        val map1 = AWMap<String, CCounter>()
        val map2 = AWMap<String, CCounter>()

        map1.set(ID1, "test", CCounter())
        map1.remove("test")
        map2.remove("test")

        assertEquals(mapOf(), map1.merge(map2))
    }

    @Test
    fun `AWMap - merge with another map with MVRegister elements with conflicting removes`() {
        val map1 = AWMap<String, MVRegister<Int>>()
        val map2 = AWMap<String, MVRegister<Int>>()

        map1.set(ID1, "test", MVRegister())
        map1.remove("test")
        map2.remove("test")

        assertEquals(mapOf(), map1.merge(map2))
    }

    @Test
    fun `AWMap - merge with another map with AWSet elements with conflicting adds and removes`() {
        val map1 = AWMap<String, AWSet<Int>>()
        val map2 = AWMap<String, AWSet<Int>>()

        map1.set(ID1, "test", AWSet())
        map1.remove("test")
        map2.set(ID2, "test", AWSet())

        assertEquals(mapOf("test" to AWSet()), map1.merge(map2))
    }

    @Test
    fun `AWMap - merge with another map with CCounter elements with conflicting adds and removes`() {
        val map1 = AWMap<String, CCounter>()
        val map2 = AWMap<String, CCounter>()

        map1.set(ID1, "test", CCounter())
        map1.remove("test")
        map2.set(ID2, "test", CCounter())

        assertEquals(mapOf("test" to CCounter()), map1.merge(map2))
    }

    @Test
    fun `AWMap - merge with another map with MVRegister elements with conflicting adds and removes`() {
        val map1 = AWMap<String, MVRegister<Int>>()
        val map2 = AWMap<String, MVRegister<Int>>()

        map1.set(ID1, "test", MVRegister())
        map1.remove("test")
        map2.set(ID2, "test", MVRegister())

        assertEquals(mapOf("test" to MVRegister()), map1.merge(map2))
    }

    @Test
    fun `AWMap - merge with another map with AWSet elements with conflicting values`() {
        val map1 = AWMap<String, AWSet<Int>>()
        val map2 = AWMap<String, AWSet<Int>>()

        map1.set(ID1, "test", AWSet(map1.dots))
        map1.get("test")?.add(ID1, 1)
        assertSame(map1.dots, map1.get("test")?.dots)

        map2.set(ID2, "test", AWSet(map2.dots))
        map2.get("test")?.add(ID2, 2)
        assertSame(map2.dots, map2.get("test")?.dots)

        assertEquals(
            mapOf(
                "test" to
                    AWSet(
                        mutableSetOf(
                            DottedValue(ID1, 2, 1),
                            DottedValue(ID2, 2, 2),
                        ),
                        DotsContext(
                            mutableMapOf(
                                ID1 to 2,
                                ID2 to 2,
                            ),
                        ),
                    ),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with CCounter elements with conflicting values`() {
        val map1 = AWMap<String, CCounter>()
        val map2 = AWMap<String, CCounter>()

        map1.set(ID1, "test", CCounter(map1.dots))
        map1.get("test")?.inc(ID1, 10)
        map2.set(ID2, "test", CCounter(map2.dots))
        map2.get("test")?.dec(ID2, 3)

        assertEquals(
            mapOf(
                "test" to
                    CCounter(
                        mutableSetOf(
                            DottedValue(ID1, 2, 10),
                            DottedValue(ID2, 2, -3),
                        ),
                        DotsContext(
                            mutableMapOf(
                                ID1 to 2,
                                ID2 to 2,
                            ),
                        ),
                    ),
            ),
            map1.merge(map2),
        )
    }

    @Test
    fun `AWMap - merge with another map with MVRegister elements with conflicting values`() {
        val map1 = AWMap<String, MVRegister<Int>>()
        val map2 = AWMap<String, MVRegister<Int>>()

        map1.set(ID1, "test", MVRegister(map1.dots))
        map1.get("test")?.assign(ID1, 1)
        map2.set(ID2, "test", MVRegister(map2.dots))
        map2.get("test")?.assign(ID2, 2)

        assertEquals(
            mapOf(
                "test" to
                    MVRegister(
                        mutableSetOf(
                            DottedValue(ID1, 2, 1),
                            DottedValue(ID2, 2, 2),
                        ),
                        DotsContext(
                            mutableMapOf(
                                ID1 to 2,
                                ID2 to 2,
                            ),
                        ),
                    ),
            ),
            map1.merge(map2),
        )
    }
}
