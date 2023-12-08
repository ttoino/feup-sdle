package pt.up.fe.sdle.crdt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private fun <V : Any> f(
    a: Set<DottedValue<V>>,
    b: DotsContext,
): Set<DottedValue<V>> = a.filter { !b.has(it.dot) }.toSet()

@Serializable(AWSetSerializer::class)
class AWSet<V : Any>(
    var _value: MutableSet<DottedValue<V>> = mutableSetOf(),
    val dots: DotsContext = DotsContext(),
) : DotsCRDT<AWSet<V>> {
    val value: Set<V> get() = _value.map { it.value }.toSet()

    constructor(dots: DotsContext) : this(mutableSetOf(), dots)

    fun add(
        id: String,
        v: V,
    ): Set<V> {
        val dot = dots.next(id)
        _value.add(DottedValue(dot, v))
        return value
    }

    fun remove(v: V): Set<V> {
        _value.removeIf { it.value == v }
        return value
    }

    fun reset(): Set<V> {
        _value.clear()
        return value
    }

    override fun merge(
        other: AWSet<V>,
        mergeDots: Boolean,
    ): Set<V> {
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

    override fun toString(): String = "AWSet(${_value}, $dots)"
}

class AWSetSerializer<V : Any>(valueSerializer: KSerializer<V>) : KSerializer<AWSet<V>> {
    private val delegateSerializer = AWSetSurrogate.serializer(valueSerializer)
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: AWSet<V>,
    ) {
        val surrogate = AWSetSurrogate(value._value, value.dots)
        encoder.encodeSerializableValue(delegateSerializer, surrogate)
    }

    override fun deserialize(decoder: Decoder): AWSet<V> {
        val surrogate = decoder.decodeSerializableValue(delegateSerializer)
        return AWSet(surrogate.value.toMutableSet(), surrogate.dots)
    }

    @Serializable
    @SerialName("AWSet")
    data class AWSetSurrogate<V : Any>(val value: Iterable<DottedValue<V>>, val dots: DotsContext)
}
