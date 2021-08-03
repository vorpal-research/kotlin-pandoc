package ru.spbstu.pandoc.kxserialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlin.reflect.KProperty1

@ExperimentalSerializationApi
class TupleLikeDescriptor(override val serialName: String,
                          vararg elementDescriptorsGen: () -> SerialDescriptor) : SerialDescriptor {
    val elementDescriptors = elementDescriptorsGen.map { lazy(it) }
    private val size get() = elementDescriptors.size
    override val kind: SerialKind get() = StructureKind.LIST
    override val elementsCount: Int get() = size

    private val indexRange get() = 0 until elementsCount
    private fun requireValidIndex(index: Int) {
        require(index in indexRange) { "Illegal index $index, $serialName expects only indices in range $indexRange"}
    }

    override fun getElementName(index: Int): String = index.toString()
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("${'$'}name is not a valid list index")

    override fun isElementOptional(index: Int): Boolean {
        requireValidIndex(index)
        return false
    }

    override fun getElementAnnotations(index: Int): List<Annotation> {
        requireValidIndex(index)
        return emptyList()
    }

    override fun getElementDescriptor(index: Int): SerialDescriptor {
        requireValidIndex(index)
        return elementDescriptors[index].value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TupleLikeDescriptor) return false
        if (elementDescriptors.equals(other.elementDescriptors)
            && serialName == other.serialName) return true
        return false
    }

    override fun hashCode(): Int {
        return elementDescriptors.hashCode() * 31 + serialName.hashCode()
    }

    override fun toString(): String =
        serialName + elementDescriptors.joinToString(prefix = "(", postfix = ")")
}

data class TupleSerializerProp<Base, out R>(val prop: KProperty1<Base, R>,
                                            val serializerGen: () -> KSerializer<@UnsafeVariance R>)
inline fun <reified Base, reified R> tupleProp(prop: KProperty1<Base, R>): TupleSerializerProp<Base, R> =
    TupleSerializerProp(prop) { serializerFor(prop) }

inline fun <reified Base, reified T0> TupleLikeSerializer(
    crossinline constructor: (T0) -> Base,
    prop: TupleSerializerProp<Base, T0>
) = object : KSerializer<Base> {
    val tls = TupleLikeSerializer(
        "${Base::class}",
        prop
    )
    override val descriptor: SerialDescriptor
        get() = tls.descriptor

    override fun deserialize(decoder: Decoder): Base = constructRaw(constructor, tls.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: Base) = tls.serialize(encoder, arrayOf(prop.prop(value)))
}

inline fun <reified Base, reified T0, reified T1> TupleLikeSerializer(
    crossinline constructor: (T0, T1) -> Base,
    prop0: TupleSerializerProp<Base, T0>,
    prop1: TupleSerializerProp<Base, T1>
) = object : KSerializer<Base> {
    val tls = TupleLikeSerializer(
        "${Base::class}",
        prop0, prop1
    )
    override val descriptor: SerialDescriptor
        get() = tls.descriptor

    override fun deserialize(decoder: Decoder): Base =
        constructRaw(constructor, tls.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: Base) =
        tls.serialize(encoder, arrayOf(prop0.prop(value), prop1.prop(value)))
}

inline fun <reified Base, reified T0, reified T1, reified T2> TupleLikeSerializer(
    crossinline constructor: (T0, T1, T2) -> Base,
    prop0: TupleSerializerProp<Base, T0>,
    prop1: TupleSerializerProp<Base, T1>,
    prop2: TupleSerializerProp<Base, T2>,
) = object : KSerializer<Base> {
    val tls = TupleLikeSerializer(
        "${Base::class}",
        prop0, prop1, prop2
    )
    override val descriptor: SerialDescriptor
        get() = tls.descriptor

    override fun deserialize(decoder: Decoder): Base =
        constructRaw(constructor, tls.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: Base) =
        tls.serialize(encoder, arrayOf(prop0.prop(value), prop1.prop(value), prop2.prop(value)))
}

open class TupleLikeSerializer(
    val serialName: String,
    vararg props: TupleSerializerProp<*, *>
): KSerializer<Array<Any?>> {
    val elementSerializersLazy = props.map { lazy(it.serializerGen) }
    override val descriptor: SerialDescriptor =
        TupleLikeDescriptor(serialName,
            elementDescriptorsGen = elementSerializersLazy.map { { it.value.descriptor } }.toTypedArray())

    override fun deserialize(decoder: Decoder): Array<Any?> {
        val UNINIT = Any()
        val result: Array<Any?> = Array(elementSerializersLazy.size) { UNINIT }
        decoder.decodeStructure(descriptor) {
            forEachIndex(descriptor, elementSerializersLazy.indices) { index ->
                result[index] =
                    decodeSerializableElement(
                        descriptor,
                        index,
                        elementSerializersLazy[index].value as KSerializer<Any?>,
                        result[index].takeUnless { it === UNINIT }
                    )
            }
        }
        check(result.all { it !== UNINIT })
        return result
    }
    override fun serialize(encoder: Encoder, value: Array<Any?>) {
        encoder.encodeCollection(descriptor, elementSerializersLazy.size) {
            elementSerializersLazy.indices.forEach { i ->
                encodeSerializableElement(descriptor, i, elementSerializersLazy[i].value as KSerializer<Any?>, value[i])
            }
        }
    }

}