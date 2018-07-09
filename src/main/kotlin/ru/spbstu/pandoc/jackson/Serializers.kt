package ru.spbstu.pandoc.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

@Suppress("UNCHECKED_CAST")
class PandocSerializer<T: Any>(val clazz: KClass<T>) : StdSerializer<T>(clazz.java) {
    private val KClass<*>.sortedProperties
        get(): List<KProperty1<*, *>> {
            val parameters = primaryConstructor?.parameters ?: listOf()
            return parameters.map { param ->
                declaredMemberProperties.find { it.name == param.name }
            }.map { it ?: throw IllegalArgumentException() }
        }

    override fun serialize(value: T, gen: JsonGenerator, sp: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("t", value::class.simpleName)
        val props = value::class.sortedProperties as Collection<KProperty1<T, Any?>>
        when {
            props.isEmpty() -> {}
            props.size == 1 -> gen.writeObjectField("c", props.first()(value))
            else -> {
                gen.writeArrayFieldStart("c")
                props.forEach { prop ->
                    gen.writeObject(prop(value))
                }
                gen.writeEndArray()
            }
        }
        gen.writeEndObject()
    }
}
