package pt.up.fe.sdle.crdt

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(DotsContextSerializer::class)
class DotsContext(
    val dots: MutableMap<String, Int> = mutableMapOf(),
) {
    fun max(id: String): Int = dots[id] ?: 0

    fun next(id: String): Dot {
        val next = max(id) + 1
        dots[id] = next
        return Dot(id, next)
    }

    fun has(
        id: String,
        dot: Int,
    ): Boolean = max(id) >= dot

    fun has(dot: Dot): Boolean = has(dot.id, dot.n)

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

    override fun toString(): String = "DotsContext($dots)"
}

@OptIn(ExperimentalSerializationApi::class)
object DotsContextSerializer : KSerializer<DotsContext> {
    private val delegateSerializer = MapSerializer(serializer<String>(), serializer<Int>())
    override val descriptor: SerialDescriptor = SerialDescriptor("DotsContext", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: DotsContext,
    ) {
        encoder.encodeSerializableValue(delegateSerializer, value.dots)
    }

    override fun deserialize(decoder: Decoder): DotsContext {
        val dots = decoder.decodeSerializableValue(delegateSerializer)
        return DotsContext(dots.toMutableMap())
    }
}

@Serializable(DotSerializer::class)
data class Dot(val id: String, val n: Int)

@OptIn(ExperimentalSerializationApi::class)
object DotSerializer : KSerializer<Dot> {
    private val delegateSerializer = ListSerializer(serializer<Any>())
    override val descriptor: SerialDescriptor = SerialDescriptor("Dot", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: Dot,
    ) {
        encoder.encodeSerializableValue(delegateSerializer, listOf(value.id, value.n))
    }

    override fun deserialize(decoder: Decoder): Dot {
        val value = decoder.decodeSerializableValue(delegateSerializer)
        return Dot(value[0] as String, value[1] as Int)
    }
}

@Serializable(DottedValueSerializer::class)
data class DottedValue<out V : Any>(val dot: Dot, val value: V) {
    val id get() = dot.id
    val n get() = dot.n

    constructor(id: String, n: Int, value: V) : this(Dot(id, n), value)
}

@OptIn(ExperimentalSerializationApi::class)
class DottedValueSerializer<V : Any> : KSerializer<DottedValue<V>> {
    private val delegateSerializer = ListSerializer(serializer<Any>())
    override val descriptor: SerialDescriptor = SerialDescriptor("DottedValue", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: DottedValue<V>,
    ) {
        encoder.encodeSerializableValue(
            delegateSerializer,
            listOf(
                value.id,
                value.n,
                value.value,
            ),
        )
    }

    override fun deserialize(decoder: Decoder): DottedValue<V> {
        val value = decoder.decodeSerializableValue(delegateSerializer)
        return DottedValue(value[0] as String, value[1] as Int, value[2] as V)
    }
}
