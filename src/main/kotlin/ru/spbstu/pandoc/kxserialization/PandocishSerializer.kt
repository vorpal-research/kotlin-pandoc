@file:OptIn(InternalSerializationApi::class)

package ru.spbstu.pandoc.kxserialization

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import ru.spbstu.pandoc.kxserialization.raw.OverrideDescriptorDecoder
import ru.spbstu.pandoc.kxserialization.raw.OverrideDescriptorEncoder
import ru.spbstu.pandoc.kxserialization.raw.RawDecoder
import ru.spbstu.pandoc.kxserialization.raw.RawValueDecoder
import kotlin.reflect.KClass

inline fun <T> wrapSerializer(
    serializer: KSerializer<T>,
    crossinline transformEncoder: (Encoder) -> Encoder,
    crossinline transformDecoder: (Decoder) -> Decoder,
    noinline trasformDescriptor: ((SerialDescriptor) -> SerialDescriptor)? = null
) = when (trasformDescriptor) {
    null -> object : KSerializer<T> by serializer {
        override fun serialize(encoder: Encoder, value: T) = serializer.serialize(transformEncoder(encoder), value)
        override fun deserialize(decoder: Decoder): T = serializer.deserialize(transformDecoder(decoder))
    }
    else -> object : KSerializer<T> by serializer {
        override val descriptor: SerialDescriptor by lazy { trasformDescriptor(serializer.descriptor) }
        override fun serialize(encoder: Encoder, value: T) =
            serializer.serialize(transformEncoder(OverrideDescriptorEncoder(encoder, descriptor)), value)

        override fun deserialize(decoder: Decoder): T =
            serializer.deserialize(transformDecoder(OverrideDescriptorDecoder(decoder, descriptor)))
    }
}

inline fun <reified T: Enum<T>> pandocEnumSerializer() = object : PandocEnumSerializer<T>("${T::class}") {
    override fun valueOf(key: String): T = enumValueOf(key)
    override fun values(): Array<T> = enumValues()
}

abstract class PandocEnumSerializer<T : Enum<T>>(serialName: String) : KSerializer<T> {
    abstract fun valueOf(key: String): T
    abstract fun values(): Array<T>

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(serialName) {
        element("t", serialDescriptor<String>())
    }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                valueOf(decodeStringElement(descriptor, 0))
            } else {
                while (true) {
                    when (val i = decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> error("Value for key \"t\" not present")
                        0 -> return@decodeStructure valueOf(decodeStringElement(descriptor, 0))
                        else -> error("Illegal index: $i")
                    }
                }
                valueOf(decodeStringElement(descriptor, 0))
            }
        }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
        }
    }
}

inline fun <reified T: Any> PolymorphicPandocishSerializer(vararg entries: Pair<KClass<out T>, Lazy<KSerializer<out T>>>) =
    PolymorphicPandocishSerializer("${T::class}", mapOf(*entries))

open class PolymorphicPandocishSerializer<T : Any>(
    val serialName: String,
    val subclassSerializers: Map<KClass<out T>, Lazy<KSerializer<out T>>>
) : KSerializer<T> {
    private val SerialDescriptor.simpleName
        get() = serialName.replaceBeforeLast('.', "").removePrefix(".")

    val serialNameToSerializer by lazy { subclassSerializers.mapKeys { it.value.value.descriptor.simpleName } }

    private fun getSerializer(serialName: String): KSerializer<T> =
        serialNameToSerializer[serialName]?.value?.uncheckedCast()
            ?: throw SerializationException("Unknown sealed subclass: $serialName")

    private fun getSerializer(klass: KClass<out T>): KSerializer<T> =
        subclassSerializers[klass]?.value?.uncheckedCast()
            ?: throw SerializationException("Unsupported sealed subclass: $klass")

    override val descriptor: SerialDescriptor = lazyDescriptor(
        serialName = serialName, kind = StructureKind.CLASS, elementsCount = 2
    ) {
        buildClassSerialDescriptor(serialName) {
            element("t", serialDescriptor<String>())
            val elementDescriptor =
                buildSerialDescriptor("kotlinx.serialization.Sealed<${serialName}>", SerialKind.CONTEXTUAL) {
                    serialNameToSerializer.forEach { s, kSerializer ->
                        element(s, kSerializer.value.descriptor)
                    }
                }
            element("c", elementDescriptor)
        }
    }

    override fun serialize(encoder: Encoder, value: T) {
        val sub = getSerializer(value::class)
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, sub.descriptor.simpleName)
            if (sub.descriptor.kind != StructureKind.OBJECT)
                encodeSerializableElement(descriptor, 1, sub, value)
        }
    }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val actualClass = decodeStringElement(descriptor, 0)
                val sub = getSerializer(actualClass)
                decodeSerializableElement(descriptor, 1, sub)
            } else {
                var actualClass: String? = null
                var returnValue: T? = null
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> break
                        0 -> actualClass = decodeStringElement(descriptor, 0)
                        1 -> {
                            checkNotNull(actualClass)
                            val sub = getSerializer(actualClass)
                            returnValue = decodeSerializableElement(descriptor, 1, sub, returnValue)
                        }
                    }
                }
                if (returnValue == null && actualClass != null) {
                    val sub = getSerializer(actualClass)
                    returnValue = RawValueDecoder(serializersModule, mapOf<String, Any?>()).decodeSerializableValue(sub)
                }
                check(returnValue != null)
                returnValue
            }

        }
}