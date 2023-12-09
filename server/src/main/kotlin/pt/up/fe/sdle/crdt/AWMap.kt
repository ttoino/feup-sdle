@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.crdt

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.*

interface DotsCRDT<T : Any> {
    fun merge(
        other: T,
        mergeDots: Boolean = true,
    ): Any
}

abstract class BaseDotsCRDT<T : DotsCRDT<T>> : DotsCRDT<T>

@Serializable(AWMapSerializer::class)
class AWMap<K : Any, V : DotsCRDT<V>>(
    val _set: AWSet<K>,
    val map: MutableMap<K, V> = mutableMapOf(),
    var _dots: DotsContext = DotsContext(),
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

class AWMapSerializer<K : Any, V : DotsCRDT<V>>(keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>) :
    KSerializer<AWMap<K, V>> {
    private val delegateSerializer = AWMapSurrogate.serializer(keySerializer, valueSerializer)
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: AWMap<K, V>,
    ) {
        val surrogate = AWMapSurrogate(value._set, value.map.map { AWMapEntry(it.key, it.value) })
        encoder.encodeSerializableValue(delegateSerializer, surrogate)
    }

    override fun deserialize(decoder: Decoder): AWMap<K, V> {
        val surrogate = decoder.decodeSerializableValue(delegateSerializer)
        val map = surrogate.map.map { Pair(it.key, it.value) }.toTypedArray()
        return AWMap(surrogate.keys, mutableMapOf(*map))
    }

    @Serializable
    @SerialName("AWMap")
    data class AWMapSurrogate<K : Any, V : DotsCRDT<V>>(val keys: AWSet<K>, val map: List<AWMapEntry<K, V>>)
}

@Serializable(AWMapEntrySerializer::class)
data class AWMapEntry<K : Any, V : DotsCRDT<V>>(val key: K, val value: V)

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class AWMapEntrySerializer<K : Any, V : DotsCRDT<V>>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>,
) : KSerializer<AWMapEntry<K, V>> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("AWMapEntry", StructureKind.LIST) {
            element("key", keySerializer.descriptor)
            element("value", valueSerializer.descriptor)
        }

    override fun serialize(
        encoder: Encoder,
        value: AWMapEntry<K, V>,
    ) {
        encoder.encodeCollection(descriptor, 2) {
            encodeSerializableElement(descriptor, 0, keySerializer, value.key)
            encodeSerializableElement(descriptor, 1, valueSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): AWMapEntry<K, V> =
        decoder.decodeStructure(descriptor) {
            decodeCollectionSize(descriptor)

            var key: K? = null
            var value: V? = null
            var index: Int = decodeElementIndex(descriptor)

            while (index != CompositeDecoder.DECODE_DONE) {
                when (index) {
                    0 -> key = decodeSerializableElement(descriptor, index, keySerializer)
                    1 -> value = decodeSerializableElement(descriptor, index, valueSerializer)
                    else -> error("Unknown index")
                }

                index = decodeElementIndex(descriptor)
            }

            if (key != null && value != null) {
                AWMapEntry(key, value)
            } else {
                error("Could not decode AWMapEntry")
            }
        }
}
