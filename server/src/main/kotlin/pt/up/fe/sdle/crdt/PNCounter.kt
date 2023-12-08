package pt.up.fe.sdle.crdt

class PNCounter(
    private val increments: GCounter = GCounter(),
    private val decrements: GCounter = GCounter(),
) {
    constructor(increments: Int = 0, decrements: Int = 0) :
        this(GCounter(increments), GCounter(decrements))

    fun getValue(): Int {
        return increments.getValue() - decrements.getValue()
    }

    fun inc(v: Int = 1): Int {
        increments.inc(v)
        return getValue()
    }

    fun dec(v: Int = 1): Int {
        decrements.inc(v)
        return getValue()
    }

    fun merge(other: PNCounter): Int {
        increments.merge(other.increments)
        decrements.merge(other.decrements)
        return getValue()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PNCounter

        if (increments != other.increments) return false
        if (decrements != other.decrements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = increments.hashCode()
        result = 31 * result + decrements.hashCode()
        return result
    }

    override fun toString(): String = "AWMap($increments, $decrements)"
}
