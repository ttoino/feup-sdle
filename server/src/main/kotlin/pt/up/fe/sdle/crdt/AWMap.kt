package pt.up.fe.sdle.crdt

interface DotsCRDT<T : DotsCRDT<T>> {
    fun merge(
        other: T,
        mergeDots: Boolean = true,
    ): Any
}

class AWMap<K, V : DotsCRDT<V>>(
    val _set: AWSet<K>,
    val map: MutableMap<K, V> = mutableMapOf(),
    val _dots: DotsContext = DotsContext(),
) : DotsCRDT<AWMap<K, V>> {
    val value: Map<K, V> get() = map
    val dots get() = _dots

    constructor(dots: DotsContext = DotsContext()) : this(AWSet(dots), mutableMapOf(), dots)

    fun get(key: K): V? = map[key]

    fun set(
        id: String,
        key: K,
        value: V,
        mergeDots: Boolean = true,
    ): Map<K, V> {
        if (key !in _set.value) {
            _set.add(id, key)
        }

        if (key !in map) {
            map[key] = value
        } else {
            map[key]!!.merge(value, mergeDots)
        }

        return this.value
    }

    fun remove(key: K): Map<K, V> {
        _set.remove(key)
        map.remove(key)
        return this.value
    }

    override fun merge(
        other: AWMap<K, V>,
        mergeDots: Boolean,
    ): Map<K, V> {
        _set.merge(other._set, false)

        for (k in map.keys)
            if (k !in _set.value) {
                map.remove(k)
            }

        for ((k, v) in other.map)
            if (k in _set.value) {
                set("", k, v, false)
            }

        if (mergeDots) _dots.merge(other._dots)

        return this.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AWMap<*, *>

        if (_set != other._set) return false
        if (map != other.map) return false
        if (_dots != other._dots) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _set.hashCode()
        result = 31 * result + map.hashCode()
        result = 31 * result + _dots.hashCode()
        return result
    }

    override fun toString(): String = "AWMap(${_set}, $map, ${_dots})"
}
