package ru.spbstu.pandoc.kxserialization.raw

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

class WrappedDecoder(val composite: CompositeDecoder, val descriptor: SerialDescriptor, val index: Int): Decoder {
    override val serializersModule: SerializersModule
        get() = composite.serializersModule
    override fun decodeBoolean(): Boolean = composite.decodeBooleanElement(descriptor, index)
    override fun decodeByte(): Byte = composite.decodeByteElement(descriptor, index)
    override fun decodeChar(): Char = composite.decodeCharElement(descriptor, index)
    override fun decodeDouble(): Double = composite.decodeDoubleElement(descriptor, index)
    override fun decodeFloat(): Float = composite.decodeFloatElement(descriptor, index)
    override fun decodeInt(): Int = composite.decodeIntElement(descriptor, index)
    override fun decodeLong(): Long = composite.decodeLongElement(descriptor, index)
    override fun decodeShort(): Short = composite.decodeShortElement(descriptor, index)
    override fun decodeString(): String = composite.decodeStringElement(descriptor, index)
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = enumDescriptor.getElementIndex(decodeString())

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? =
        composite.decodeNullableSerializableElement(descriptor, index, deserializer)

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        composite.decodeSerializableElement(descriptor, index, deserializer)

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = error("decodeNotNullMark() called inside decodeInline()")
    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = error("decodeNull() called inside decodeInline()")
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        error("beginStructure() called inside decodeInline()")
    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder =
        error("decodeInline() called inside decodeInline()")
}

class UnwrappedDecoder(val base: Decoder): Decoder by base, CompositeDecoder {
    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        decodeBoolean()
    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        decodeByte()
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        decodeChar()
    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        decodeDouble()
    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        decodeFloat()
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        decodeInt()
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        decodeLong()
    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        decodeShort()
    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        decodeString()
    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = decodeInline(descriptor)
    @ExperimentalSerializationApi
    override fun decodeSequentially(): Boolean = true

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? = base.decodeNullableSerializableValue(deserializer)


    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T = base.decodeSerializableValue(deserializer)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = this
    override fun endStructure(descriptor: SerialDescriptor) {}

    var elementProcessed: Boolean = false
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = when(elementProcessed) {
        false -> 0.also { elementProcessed = true }
        true -> CompositeDecoder.DECODE_DONE
    }
}