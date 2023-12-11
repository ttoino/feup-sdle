package pt.up.fe.sdle.crdt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@Serializable(CCounterSerializer::class)
class CCounter(
    val set: AWSet<Int> = AWSet(),
) : DotsCRDT<CCounter> {
    val value get() = set._value.sumOf {
        it.value
    }

    constructor(set: MutableSet<DottedValue<Int>>, dots: DotsContext) : this(AWSet(set, dots))
    constructor(dots: DotsContext) : this(mutableSetOf(), dots)

    fun inc(
        id: String,
        i: Int = 1,
    ): Int {
        var v = 0

        for (it in set._value) {
            if (it.id != id) {
                continue
            }

            v = it.value
            set._value.remove(it)
        }

        v += i

        set.add(id, v)

        return value
    }

    fun dec(
        id: String,
        i: Int = 1,
    ): Int = inc(id, -i)

    override fun merge(
        other: CCounter,
        mergeDots: Boolean,
    ): Int {
        set.merge(other.set, mergeDots)
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CCounter

        return set == other.set
    }

    override fun hashCode(): Int = set.hashCode()

    override fun toString(): String = "CCounter($set)"
}

@OptIn(ExperimentalSerializationApi::class)
object CCounterSerializer : KSerializer<CCounter> {
    private val delegateSerializer = AWSetSerializer(serializer<Int>())
    override val descriptor: SerialDescriptor = SerialDescriptor("CCounter", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: CCounter,
    ) {
        encoder.encodeSerializableValue(delegateSerializer, value.set)
    }

    override fun deserialize(decoder: Decoder): CCounter = CCounter(decoder.decodeSerializableValue(delegateSerializer))
}
