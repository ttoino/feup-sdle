package pt.up.fe.sdle.crdt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(GCounterSerializer::class)
class GCounter(
    private var _value: Int = 0,
) {
    val value get() = _value

    fun inc(v: Int = 1): Int {
        if (v < 0) throw IllegalArgumentException("Cannot decrement a GCounter")

        _value += v
        return _value
    }

    fun merge(other: GCounter): Int {
        _value = maxOf(_value, other._value)
        return _value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GCounter

        return _value == other._value
    }

    override fun hashCode(): Int = _value

    override fun toString(): String = "GCounter($_value)"
}

object GCounterSerializer : KSerializer<GCounter> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GCounter", PrimitiveKind.INT)

    override fun serialize(
        encoder: Encoder,
        value: GCounter,
    ) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): GCounter = GCounter(decoder.decodeInt())
}
