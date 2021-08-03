package ru.spbstu.pandoc.kxserialization.raw

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

private val NOT_INIT = Any()

sealed class RawEncoder: Encoder {
    var value: Any? = NOT_INIT
        protected set
}

open class RawValueEncoder(override val serializersModule: SerializersModule) : RawEncoder() {
    override fun encodeBoolean(value: Boolean) {
        this.value = value
    }

    override fun encodeByte(value: Byte) {
        this.value = value
    }

    override fun encodeChar(value: Char) {
        this.value = value
    }

    override fun encodeDouble(value: Double) {
        this.value = value
    }

    override fun encodeFloat(value: Float) {
        this.value = value
    }

    override fun encodeInt(value: Int) {
        this.value = value
    }

    override fun encodeLong(value: Long) {
        this.value = value
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        this.value = null
    }

    override fun encodeShort(value: Short) {
        this.value = value
    }

    override fun encodeString(value: String) {
        this.value = value
    }
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        this.value = enumDescriptor.getElementName(index)
    }

    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder = this

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = when (descriptor.kind) {
        StructureKind.LIST -> ListEncoder(this)
        else -> MapEncoder(this)
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder = when (descriptor.kind) {
        StructureKind.LIST -> ListEncoder(this, collectionSize)
        else -> MapEncoder(this)
    }

    class MapEncoder(parent: RawEncoder): RawValueEncoder(parent.serializersModule), CompositeEncoder {
        init {
            value = mutableMapOf<String, Any?>()
        }

        val map get() = value as MutableMap<String, Any?>
        fun push(descriptor: SerialDescriptor, index: Int, value: Any?) {
            map[descriptor.getElementName(index)] = value
        }
        override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
            push(descriptor, index, value)
        override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
            push(descriptor, index, value)
        override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
            push(descriptor, index, value)
        override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
            push(descriptor, index, value)
        override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
            push(descriptor, index, value)
        override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
            push(descriptor, index, value)
        override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
            push(descriptor, index, value)
        override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
            push(descriptor, index, value)
        override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
            push(descriptor, index, value)
        @ExperimentalSerializationApi
        override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
            WrappedEncoder(this, descriptor, index)

        @ExperimentalSerializationApi
        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?
        ) {
            if (value == null) push(descriptor, index, null)
            else encodeSerializableElement(descriptor, index, serializer, value)
        }

        override fun <T> encodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T
        ) {
            val sub = RawValueEncoder(serializersModule)
            serializer.serialize(sub, value)
            push(descriptor, index, sub.value)
        }


        override fun endStructure(descriptor: SerialDescriptor) {}
    }

    class ListEncoder(parent: RawEncoder, initialSize: Int? = null): RawValueEncoder(parent.serializersModule), CompositeEncoder {
        init {
            when (initialSize) {
                null -> value = mutableListOf<Any?>()
                else -> value = MutableList(initialSize) { null }
            }
        }

        val list get() = value as MutableList<Any?>
        fun push(index: Int, value: Any?) {
            while (index > list.size) list.add(null)
            list[index] = value
        }
        override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
            push(index, value)
        override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
            push(index, value)
        override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
            push(index, value)
        override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
            push(index, value)
        override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
            push(index, value)
        override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
            push(index, value)
        override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
            push(index, value)
        override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
            push(index, value)
        override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
            push(index, value)
        @ExperimentalSerializationApi
        override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
            WrappedEncoder(this, descriptor, index)

        @ExperimentalSerializationApi
        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?
        ) {
            if (value == null) push(index, null)
            else encodeSerializableElement(descriptor, index, serializer, value)
        }

        override fun <T> encodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T
        ) {
            val sub = RawValueEncoder(serializersModule)
            serializer.serialize(sub, value)
            push(index, sub.value)
        }
        override fun endStructure(descriptor: SerialDescriptor) {}
    }
}