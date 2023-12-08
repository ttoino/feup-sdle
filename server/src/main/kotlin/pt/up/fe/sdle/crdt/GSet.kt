package pt.up.fe.sdle.crdt

class GSet<V>(
    private val value: MutableSet<V> = mutableSetOf(),
) {
    fun getValue(): Set<V> = value

    fun add(v: V): Set<V> {
        value.add(v)
        return value
    }

    fun merge(other: GSet<V>): Set<V> {
        value.addAll(other.value)
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GSet<*>

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String = "GSet($value)"
}
