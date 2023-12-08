package pt.up.fe.sdle.crdt

class MVRegister<V>(
    private val set: AWSet<V> = AWSet()
): DotsCRDT<MVRegister<V>> {
    val value get() = set.value

    constructor(set: MutableSet<DottedValue<V>>, dots: DotsContext): this(AWSet(set, dots))
    constructor(dots: DotsContext): this(mutableSetOf(), dots)

    fun assign(id: String, v: V): Set<V> {
        set.reset()
        return set.add(id, v)
    }

    override fun merge(other: MVRegister<V>, mergeDots: Boolean): Set<V> {
        return set.merge(other.set, mergeDots)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MVRegister<*>

        return set == other.set
    }

    override fun hashCode(): Int {
        return set.hashCode()
    }

    override fun toString(): String = "MVRegister(${set})"
}