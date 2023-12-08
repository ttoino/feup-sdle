package pt.up.fe.sdle.crdt

typealias Dot = Pair<String, Int>

class DotsContext(
    private val dots: MutableMap<String, Int> = mutableMapOf(),
) {
    fun max(id: String): Int = dots[id] ?: 0

    fun next(id: String): Dot {
        val next = max(id) + 1
        dots[id] = next
        return Pair(id, next)
    }

    fun has(id: String, dot: Int): Boolean = max(id) >= dot

    fun has(dot: Dot): Boolean = has(dot.first, dot.second)

    fun merge(other: DotsContext): DotsContext {
        for ((id, dot) in other.dots)
            dots[id] = maxOf(max(id), dot)

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DotsContext

        return dots == other.dots
    }

    override fun hashCode(): Int = dots.hashCode()

    override fun toString(): String = "DotsContext(${dots})"
}