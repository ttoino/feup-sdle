package pt.up.fe.sdle.crdt

typealias DottedValue<V> = Pair<Dot, V>

private fun <V> f(a: Set<DottedValue<V>>, b: DotsContext): Set<DottedValue<V>> =
    a.filter { !b.has(it.first) }.toSet()

class AWSet<V>(
    var _value: MutableSet<DottedValue<V>> = mutableSetOf(),
    val dots: DotsContext = DotsContext(),
): DotsCRDT<AWSet<V>> {
    val value: Set<V> get() = _value.map { it.second }.toSet()

    constructor(dots: DotsContext): this(mutableSetOf(), dots)

    fun add(id: String, v: V): Set<V> {
        val dot = dots.next(id)
        _value.add(Pair(dot, v))
        return value
    }

    fun remove(v: V): Set<V> {
        _value.removeIf { it.second == v }
        return value
    }

    fun reset(): Set<V> {
        _value.clear()
        return value
    }

    override fun merge(other: AWSet<V>, mergeDots: Boolean): Set<V> {
        val a = f(_value, other.dots)
        val b = f(other._value, dots)

        _value = _value.intersect(other._value).union(a).union(b).toMutableSet()

        if (mergeDots) dots.merge(other.dots)

        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AWSet<*>

        if (_value != other._value) return false
        if (dots != other.dots) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _value.hashCode()
        result = 31 * result + dots.hashCode()
        return result
    }

    override fun toString(): String = "AWSet(${_value}, ${dots})"
}