package ru.spbstu.pandoc.kxserialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlinx.warnings.Warnings

public inline fun Encoder.encodeCollection(descriptor: SerialDescriptor, size: Int, block: CompositeEncoder.() -> Unit) {
    val composite = beginCollection(descriptor, size)
    var ex: Throwable? = null
    try {
        composite.block()
    } catch (e: Throwable) {
        ex = e
        throw e
    } finally {
        // End structure only if there is no exception, otherwise it can be swallowed
        if (ex == null) composite.endStructure(descriptor)
    }
}

inline fun CompositeDecoder.forEachIndex(descriptor: SerialDescriptor, indices: IntRange, body: (Int) -> Unit) {
    if (decodeSequentially()) {
        indices.forEach(body)
    } else {
        while (true) {
            val index = decodeElementIndex(descriptor)
            if (index == CompositeDecoder.DECODE_DONE) break

            check(index in indices)
            body(index)
        }
    }
}

fun lazyDescriptor(
    annotations: List<Annotation>? = null,
    elementsCount: Int? = null,
    isInline: Boolean? = null,
    isNullable: Boolean? = null,
    serialName: String? = null,
    kind: SerialKind? = null,
    body: () -> SerialDescriptor) = object : SerialDescriptor {
    val delegate by lazy(body)
    @ExperimentalSerializationApi
    override val annotations: List<Annotation> get() = annotations ?: delegate.annotations
    @ExperimentalSerializationApi
    override val elementsCount: Int get() = elementsCount ?: delegate.elementsCount
    @ExperimentalSerializationApi
    override val isInline: Boolean get() = isInline ?: delegate.isInline
    @ExperimentalSerializationApi
    override val isNullable: Boolean get() = isNullable ?: delegate.isNullable
    @ExperimentalSerializationApi
    override val kind: SerialKind get() = kind ?: delegate.kind
    @ExperimentalSerializationApi
    override val serialName: String get() = serialName ?: delegate.serialName
    @ExperimentalSerializationApi
    override fun getElementAnnotations(index: Int): List<Annotation> = delegate.getElementAnnotations(index)
    @ExperimentalSerializationApi
    override fun getElementDescriptor(index: Int): SerialDescriptor = delegate.getElementDescriptor(index)
    @ExperimentalSerializationApi
    override fun getElementIndex(name: String): Int = delegate.getElementIndex(name)
    @ExperimentalSerializationApi
    override fun getElementName(index: Int): String = delegate.getElementName(index)
    @ExperimentalSerializationApi
    override fun isElementOptional(index: Int): Boolean = delegate.isElementOptional(index)
}

inline fun <U, reified T> serializerFor(prop: (U) -> T) = serializer<T>()

inline fun <reified T0, reified R> constructRaw(constructor: (T0) -> R, arguments: Array<out Any?>): R {
    val (v0) = arguments
    check(v0 is T0)
    return constructor(v0)
}

inline fun <reified T0, reified T1, reified R> constructRaw(constructor: (T0, T1) -> R, arguments: Array<out Any?>): R {
    val (v0, v1) = arguments
    check(v0 is T0)
    check(v1 is T1)
    return constructor(v0, v1)
}

inline fun <reified T0, reified T1, reified T2, reified R> constructRaw(constructor: (T0, T1, T2) -> R, arguments: Array<out Any?>): R {
    val (v0, v1, v2) = arguments
    check(v0 is T0)
    check(v1 is T1)
    check(v2 is T2)
    return constructor(v0, v1, v2)
}

@Suppress(Warnings.UNCHECKED_CAST)
fun <T, U> KSerializer<T>.uncheckedCast(): KSerializer<U> =
    this as KSerializer<U>
