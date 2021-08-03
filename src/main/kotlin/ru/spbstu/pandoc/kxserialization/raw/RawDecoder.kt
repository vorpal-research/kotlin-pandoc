package ru.spbstu.pandoc.kxserialization.raw

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

sealed class RawDecoder(val value: Any?): Decoder
open class RawValueDecoder(override val serializersModule: SerializersModule, value: Any?) : RawDecoder(value) {
    private inline fun <reified T> checkedValue(): T {
        check(value is T)
        return value
    }
    override fun decodeBoolean(): Boolean = checkedValue()
    override fun decodeByte(): Byte = checkedValue()
    override fun decodeChar(): Char = checkedValue()
    override fun decodeDouble(): Double = checkedValue()
    override fun decodeFloat(): Float = checkedValue()
    override fun decodeInt(): Int = checkedValue()
    override fun decodeLong(): Long = checkedValue()
    override fun decodeShort(): Short = checkedValue()
    override fun decodeString(): String = checkedValue()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val s = decodeString()
        return enumDescriptor.getElementIndex(s)
    }
    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = value != null

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        check(value === null)
        return null
    }

    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder = this

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = when(descriptor.kind) {
        StructureKind.LIST -> ListDecoder(this)
        else -> MapDecoder(this)
    }

    class MapDecoder(parent: RawDecoder): RawValueDecoder(parent.serializersModule, parent.value), CompositeDecoder {
        init {
            check(value is Map<*, *>)
        }
        val mapValue: Map<String, Any?> = when (value) {
            is Map<*, *> -> value as Map<String, Any?>
            null -> error("MapDecoder: value is null")
            else -> error("MapDecoder cannot decode value of class ${value::class}")
        }

        private inline fun <reified T> getChecked(descriptor: SerialDescriptor, index: Int): T {
            val key = descriptor.getElementName(index)
            return when (val elem = mapValue[key]) {
                is T -> elem
                else -> error("MapDecoder: expected value of type ${T::class}, received $elem")
            }
        }

        override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
            getChecked(descriptor, index)
        override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
            getChecked(descriptor, index)
        override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
            getChecked(descriptor, index)
        override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
            getChecked(descriptor, index)
        override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
            getChecked(descriptor, index)
        override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
            getChecked(descriptor, index)
        override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
            getChecked(descriptor, index)
        override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
            getChecked(descriptor, index)
        override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
            getChecked(descriptor, index)

        @ExperimentalSerializationApi
        override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder =
            WrappedDecoder(this, descriptor, index)
        @ExperimentalSerializationApi
        override fun <T : Any> decodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T?>,
            previousValue: T?
        ): T? {
            check(previousValue == null)
            val sub = RawValueDecoder(serializersModule, getChecked(descriptor, index))
            return sub.decodeNullableSerializableValue(deserializer)
        }

        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?
        ): T {
            check(previousValue == null)
            val sub = RawValueDecoder(serializersModule, getChecked(descriptor, index))
            return sub.decodeSerializableValue(deserializer)
        }

        override fun endStructure(descriptor: SerialDescriptor) { }

        private var currentIndex = 0
        override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
            if (currentIndex >= mapValue.size) CompositeDecoder.DECODE_DONE
            else ++currentIndex

        @ExperimentalSerializationApi
        override fun decodeSequentially(): Boolean = true
        override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = mapValue.size
    }

    class ListDecoder(parent: RawDecoder): RawValueDecoder(parent.serializersModule, parent.value), CompositeDecoder {
        init {
            check(value is Collection<*> || value is Array<*>)
        }
        val listValue: List<Any?> = when (value) {
            is List<*> -> value
            is Collection<*> -> value.toList()
            is Array<*> -> value.toList()
            null -> error("ListDecoder: value is null")
            else -> error("ListDecoder cannot decode value of class ${value::class}")
        }

        private inline fun <reified T> getChecked(index: Int): T = when(val elem = listValue[index]) {
            is T -> elem
            else -> error("ListDecoder: expected value of type ${T::class}, received $elem")
        }

        override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
            getChecked(index)
        override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
            getChecked(index)
        override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
            getChecked(index)
        override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
            getChecked(index)
        override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
            getChecked(index)
        override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
            getChecked(index)
        override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
            getChecked(index)
        override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
            getChecked(index)
        override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
            getChecked(index)

        @ExperimentalSerializationApi
        override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder =
            WrappedDecoder(this, descriptor, index)

        @ExperimentalSerializationApi
        override fun <T : Any> decodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T?>,
            previousValue: T?
        ): T? {
            check(previousValue == null)
            val sub = RawValueDecoder(serializersModule, getChecked(index))
            return sub.decodeNullableSerializableValue(deserializer)
        }

        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?
        ): T {
            check(previousValue == null)
            val sub = RawValueDecoder(serializersModule, getChecked(index))
            return sub.decodeSerializableValue(deserializer)
        }

        override fun endStructure(descriptor: SerialDescriptor) { }

        private var currentIndex = 0
        override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
            if (currentIndex >= listValue.size) CompositeDecoder.DECODE_DONE
            else ++currentIndex

        @ExperimentalSerializationApi
        override fun decodeSequentially(): Boolean = true
        override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = listValue.size
    }

}