package pt.up.fe.sdle.crdt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

interface DotsCRDT<T : DotsCRDT<T>> {
    fun merge(
        other: T,
        mergeDots: Boolean = true,
    ): Any
}

@Serializable(AWMapSerializer::class)
class AWMap<K : Any, V : DotsCRDT<V>>(
    val _set: AWSet<K>,
    val map: MutableMap<K, V> = mutableMapOf(),
    val _dots: DotsContext = DotsContext(),
) : DotsCRDT<AWMap<K, V>> {
    val value: Map<K, V> get() = map

    val dots get() = _dots

    constructor(dots: DotsContext = DotsContext()) : this(AWSet(dots), mutableMapOf(), dots)

    fun get(key: K): V? = map[key]

    fun set(
        id: String,
        key: K,
        value: V,
        mergeDots: Boolean = true,
    ): Map<K, V> {
        if (key !in _set.value) {
            _set.add(id, key)
        }

        if (key !in map) {
            map[key] = value
        } else {
            map[key]!!.merge(value, mergeDots)
        }

        return this.value
    }

    fun remove(key: K): Map<K, V> {
        _set.remove(key)
        map.remove(key)
        return this.value
    }

    override fun merge(
        other: AWMap<K, V>,
        mergeDots: Boolean,
    ): Map<K, V> {
        _set.merge(other._set, false)

        for (k in map.keys)
            if (k !in _set.value) {
                map.remove(k)
            }

        for ((k, v) in other.map)
            if (k in _set.value) {
                set("", k, v, false)
            }

        if (mergeDots) _dots.merge(other._dots)

        return this.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AWMap<*, *>

        if (_set != other._set) return false
        if (map != other.map) return false
        if (_dots != other._dots) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _set.hashCode()
        result = 31 * result + map.hashCode()
        result = 31 * result + _dots.hashCode()
        return result
    }

    override fun toString(): String = "AWMap(${_set}, $map, ${_dots})"
}

class AWMapSerializer<K : Any, V : DotsCRDT<V>>(keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>) : KSerializer<AWMap<K, V>> {
    private val delegateSerializer = AWMapSurrogate.serializer(keySerializer, valueSerializer)
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: AWMap<K, V>,
    ) {
        val surrogate = AWMapSurrogate(value._set, value.map.map { AWMapEntry(it.key, it.value) }, value.dots)
        encoder.encodeSerializableValue(delegateSerializer, surrogate)
    }

    override fun deserialize(decoder: Decoder): AWMap<K, V> {
        val surrogate = decoder.decodeSerializableValue(delegateSerializer)
        val map = surrogate.map.map { Pair(it.key, it.value) }.toTypedArray()
        return AWMap(surrogate.value, mutableMapOf(*map), surrogate.dots)
    }

    @Serializable
    @SerialName("AWMap")
    data class AWMapSurrogate<K : Any, V : DotsCRDT<V>>(val value: AWSet<K>, val map: Iterable<AWMapEntry<K, V>>, val dots: DotsContext)
}

@Serializable(AWMapEntrySerializer::class)
data class AWMapEntry<K : Any, V : DotsCRDT<V>>(val key: K, val value: V)

@OptIn(ExperimentalSerializationApi::class)
class AWMapEntrySerializer<K : Any, V : DotsCRDT<V>> : KSerializer<AWMapEntry<K, V>> {
    private val delegateSerializer = ListSerializer(serializer<Any>())
    override val descriptor: SerialDescriptor = SerialDescriptor("AWMapEntry", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: AWMapEntry<K, V>,
    ) {
        encoder.encodeSerializableValue(delegateSerializer, listOf(value.key, value.value))
    }

    override fun deserialize(decoder: Decoder): AWMapEntry<K, V> {
        val value = decoder.decodeSerializableValue(delegateSerializer)
        return AWMapEntry(value[0] as K, value[1] as V)
    }
}
