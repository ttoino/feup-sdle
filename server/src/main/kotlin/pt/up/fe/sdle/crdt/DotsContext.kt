package pt.up.fe.sdle.crdt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.serializer

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

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
object DotSerializer : KSerializer<Dot> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Dot", StructureKind.LIST) {
            element("id", serializer<String>().descriptor)
            element("n", serializer<Int>().descriptor)
        }

    override fun serialize(
        encoder: Encoder,
        value: Dot,
    ) {
        encoder.encodeCollection(descriptor, 2) {
            encodeStringElement(descriptor, 0, value.id)
            encodeIntElement(descriptor, 1, value.n)
        }
    }

    override fun deserialize(decoder: Decoder): Dot =
        decoder.decodeStructure(descriptor) {
            decodeCollectionSize(descriptor)

            var id: String? = null
            var n: Int? = null
            var index: Int = decodeElementIndex(descriptor)

            while (index != CompositeDecoder.DECODE_DONE) {
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> n = decodeIntElement(descriptor, index)
                    else -> error("Unknown index")
                }

                index = decodeElementIndex(descriptor)
            }

            if (id != null && n != null) {
                Dot(id, n)
            } else {
                error("Could not decode Dot")
            }
        }
}

@Serializable(DottedValueSerializer::class)
data class DottedValue<out V : Any>(val dot: Dot, val value: V) {
    val id get() = dot.id
    val n get() = dot.n

    constructor(id: String, n: Int, value: V) : this(Dot(id, n), value)
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class DottedValueSerializer<V : Any>(private val valueSerializer: KSerializer<V>) : KSerializer<DottedValue<V>> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("DottedValue", StructureKind.LIST) {
            element("id", serializer<String>().descriptor)
            element("n", serializer<Int>().descriptor)
            element("value", valueSerializer.descriptor)
        }

    override fun serialize(
        encoder: Encoder,
        value: DottedValue<V>,
    ) {
        encoder.encodeCollection(descriptor, 3) {
            encodeStringElement(descriptor, 0, value.id)
            encodeIntElement(descriptor, 1, value.n)
            encodeSerializableElement(descriptor, 2, valueSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): DottedValue<V> =
        decoder.decodeStructure(descriptor) {
            decodeCollectionSize(descriptor)

            var id: String? = null
            var n: Int? = null
            var value: V? = null
            var index: Int = decodeElementIndex(descriptor)

            while (index != CompositeDecoder.DECODE_DONE) {
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> n = decodeIntElement(descriptor, index)
                    2 -> value = decodeSerializableElement(descriptor, index, valueSerializer)
                    else -> error("Unknown index")
                }

                index = decodeElementIndex(descriptor)
            }

            if (id != null && n != null && value != null) {
                DottedValue(id, n, value)
            } else {
                error("Could not decode DottedValue")
            }
        }
}
