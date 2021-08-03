package ru.spbstu.pandoc.kxserialization.raw

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

class WrappedEncoder(val compositeEncoder: CompositeEncoder, val descriptor: SerialDescriptor, val index: Int): Encoder {
    override val serializersModule: SerializersModule
        get() = compositeEncoder.serializersModule

    override fun encodeBoolean(value: Boolean) = compositeEncoder.encodeBooleanElement(descriptor, index, value)
    override fun encodeByte(value: Byte) = compositeEncoder.encodeByteElement(descriptor, index, value)
    override fun encodeChar(value: Char) = compositeEncoder.encodeCharElement(descriptor, index, value)
    override fun encodeDouble(value: Double) = compositeEncoder.encodeDoubleElement(descriptor, index, value)
    override fun encodeFloat(value: Float) = compositeEncoder.encodeFloatElement(descriptor, index, value)
    override fun encodeInt(value: Int) = compositeEncoder.encodeIntElement(descriptor, index, value)
    override fun encodeLong(value: Long) = compositeEncoder.encodeLongElement(descriptor, index, value)
    override fun encodeShort(value: Short) = compositeEncoder.encodeShortElement(descriptor, index, value)
    override fun encodeString(value: String) = compositeEncoder.encodeStringElement(descriptor, index, value)
    @ExperimentalSerializationApi
    override fun encodeNull() {}

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) =
        encodeString(enumDescriptor.getElementDescriptor(index).serialName)

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
        compositeEncoder.encodeNullableSerializableElement(descriptor, index, serializer, value)
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        compositeEncoder.encodeSerializableElement(descriptor, index, serializer, value)
    }
    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder =
        error("encodeInline() called inside encodeInline()")
    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        error("beginCollection() called inside encodeInline()")
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        error("beginStructure() called inside encodeInline()")
}

class UnwrappedEncoder(val base: Encoder): Encoder by base, CompositeEncoder {
    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder = this
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = this
    override fun endStructure(descriptor: SerialDescriptor) {}

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
        encodeBoolean(value)
    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
        encodeByte(value)
    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
        encodeChar(value)
    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
        encodeDouble(value)
    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
        encodeFloat(value)
    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
        encodeInt(value)
    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
        encodeLong(value)
    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
        encodeShort(value)
    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
        encodeString(value)
    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder = encodeInline(descriptor)

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) = encodeNullableSerializableValue(serializer, value)

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) = encodeSerializableValue(serializer, value)
}