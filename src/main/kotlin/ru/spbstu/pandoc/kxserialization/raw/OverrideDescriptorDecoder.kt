package ru.spbstu.pandoc.kxserialization.raw

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

class OverrideDescriptorDecoder(val base: Decoder, val overridenDescriptor: SerialDescriptor): Decoder by base {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        OverrideDescriptorCompositeDecoder(base.beginStructure(overridenDescriptor), overridenDescriptor)
}

class OverrideDescriptorCompositeDecoder(
    val base: CompositeDecoder,
    val overridenDescriptor: SerialDescriptor) : CompositeDecoder by base {

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        base.decodeBooleanElement(overridenDescriptor, index)
    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        base.decodeByteElement(overridenDescriptor, index)
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        base.decodeCharElement(overridenDescriptor, index)

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        base.decodeDoubleElement(overridenDescriptor, index)

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        base.decodeFloatElement(overridenDescriptor, index)
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        base.decodeIntElement(overridenDescriptor, index)
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        base.decodeLongElement(overridenDescriptor, index)
    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        base.decodeShortElement(overridenDescriptor, index)
    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        base.decodeStringElement(overridenDescriptor, index)

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        base.decodeCollectionSize(overridenDescriptor)
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        base.decodeElementIndex(overridenDescriptor)

    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder =
        base.decodeInlineElement(overridenDescriptor, index)

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? = base.decodeNullableSerializableElement(overridenDescriptor, index, deserializer, previousValue)
    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T = base.decodeSerializableElement(overridenDescriptor, index, deserializer, previousValue)

    override fun endStructure(descriptor: SerialDescriptor) = base.endStructure(overridenDescriptor)
}


