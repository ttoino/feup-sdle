package pt.up.fe.sdle.crdt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(GSetSerializer::class)
class GSet<V>(
    private val _value: MutableSet<V> = mutableSetOf(),
) {
    val value get(): Set<V> = _value

    fun add(v: V): Set<V> {
        _value.add(v)
        return _value
    }

    fun merge(other: GSet<V>): Set<V> {
        _value.addAll(other._value)
        return _value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GSet<*>

        return _value == other._value
    }

    override fun hashCode(): Int = _value.hashCode()

    override fun toString(): String = "GSet($_value)"
}

@OptIn(ExperimentalSerializationApi::class)
class GSetSerializer<V>(valueSerializer: KSerializer<V>) : KSerializer<GSet<V>> {
    private val delegateSerializer = SetSerializer(valueSerializer)
    override val descriptor: SerialDescriptor = SerialDescriptor("GSet", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: GSet<V>,
    ) {
        encoder.encodeSerializableValue(delegateSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): GSet<V> {
        val value = decoder.decodeSerializableValue(delegateSerializer)
        return GSet(value.toMutableSet())
    }
}
