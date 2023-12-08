package pt.up.fe.sdle.crdt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(MVRegisterSerializer::class)
class MVRegister<V : Any>(
    val set: AWSet<V> = AWSet(),
) : DotsCRDT<MVRegister<V>> {
    val value get() = set.value

    constructor(set: MutableSet<DottedValue<V>>, dots: DotsContext) : this(AWSet(set, dots))
    constructor(dots: DotsContext) : this(mutableSetOf(), dots)

    fun assign(
        id: String,
        v: V,
    ): Set<V> {
        set.reset()
        return set.add(id, v)
    }

    override fun merge(
        other: MVRegister<V>,
        mergeDots: Boolean,
    ): Set<V> = set.merge(other.set, mergeDots)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MVRegister<*>

        return set == other.set
    }

    override fun hashCode(): Int = set.hashCode()

    override fun toString(): String = "MVRegister($set)"
}

@OptIn(ExperimentalSerializationApi::class)
class MVRegisterSerializer<V : Any>(valueSerializer: KSerializer<V>) : KSerializer<MVRegister<V>> {
    private val delegateSerializer = AWSetSerializer(valueSerializer)
    override val descriptor: SerialDescriptor = SerialDescriptor("MVRegister", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: MVRegister<V>,
    ) {
        encoder.encodeSerializableValue(delegateSerializer, value.set)
    }

    override fun deserialize(decoder: Decoder): MVRegister<V> = MVRegister(decoder.decodeSerializableValue(delegateSerializer))
}
