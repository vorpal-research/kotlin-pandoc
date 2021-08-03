package ru.spbstu.pandoc.kxserialization.raw

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

class OverrideDescriptorEncoder(val base: Encoder, val overridenDescriptor: SerialDescriptor): Encoder by base {
    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        OverrideDescriptorCompositeEncoder(base.beginCollection(overridenDescriptor, collectionSize), overridenDescriptor)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        OverrideDescriptorCompositeEncoder(base.beginStructure(overridenDescriptor), overridenDescriptor)
}

class OverrideDescriptorCompositeEncoder(val base: CompositeEncoder,
                                val overridenDescriptor: SerialDescriptor): CompositeEncoder by base {
    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
        base.encodeBooleanElement(overridenDescriptor, index, value)

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
        base.encodeByteElement(overridenDescriptor, index, value)

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
        base.encodeCharElement(overridenDescriptor, index, value)

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
        base.encodeDoubleElement(overridenDescriptor, index, value)

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
        base.encodeFloatElement(overridenDescriptor, index, value)

    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
        base.encodeInlineElement(overridenDescriptor, index)

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
        base.encodeIntElement(overridenDescriptor, index, value)

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
        base.encodeLongElement(overridenDescriptor, index, value)

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
        base.encodeShortElement(overridenDescriptor, index, value)

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
        base.encodeStringElement(overridenDescriptor, index, value)

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) = base.encodeNullableSerializableElement(overridenDescriptor, index, serializer, value)

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) = base.encodeSerializableElement(overridenDescriptor, index, serializer, value)

    override fun endStructure(descriptor: SerialDescriptor) = base.endStructure(overridenDescriptor)
}