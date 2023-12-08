package pt.up.fe.sdle.crdt

class CCounter(
    private val set: AWSet<Int> = AWSet()
): DotsCRDT<CCounter> {
    val value get() = set.value.sum()

    constructor(set: MutableSet<DottedValue<Int>>, dots: DotsContext): this(AWSet(set, dots))
    constructor(dots: DotsContext): this(mutableSetOf(), dots)

    fun inc(id: String, i: Int = 1): Int {
        var v = 0

        for (it in set._value) {
            if (it.first.first != id)
                continue

            v = it.second
            set._value.remove(it)
        }

        v += i

        set.add(id, v)

        return value
    }

    fun dec(id: String, i: Int = 1): Int = inc(id, -i)

    override fun merge(other: CCounter, mergeDots: Boolean): Int {
        set.merge(other.set, mergeDots)
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CCounter

        if (set != other.set) return false

        return true
    }

    override fun hashCode(): Int {
        return set.hashCode()
    }

    override fun toString(): String = "CCounter(${set})"
}