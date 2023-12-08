package pt.up.fe.sdle.crdt

class GCounter(
    private var value: Int = 0,
) {
    fun getValue() = value

    fun inc(v: Int = 1): Int {
        if (v < 0) throw IllegalArgumentException("Cannot decrement a GCounter")

        value += v
        return value
    }

    fun merge(other: GCounter): Int {
        value = maxOf(value, other.value)
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GCounter

        return value == other.value
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String = "GCounter($value)"
}
