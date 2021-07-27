package ru.spbstu.pandoc.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.type.TypeFactory
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaGetter

private inline infix fun Int.times(body: () -> Unit) {
    var count = this
    while (count != 0) {
        body(); --count
    }
}

@Suppress("UNCHECKED_CAST")
class PandocDeserializer<T : Any>(val clazz: KClass<T>) : StdDeserializer<T>(clazz.java) {
    val successors = clazz
            .sealedSubclasses
            .map { it.simpleName!! to (it as KClass<out T>) }
            .toMap()

    private val KClass<*>.sortedProperties
        get(): List<KProperty1<*, *>> {
            val parameters = primaryConstructor?.parameters ?: listOf()
            return parameters.map { param ->
                declaredMemberProperties.find { it.name == param.name }
            }.map { it ?: throw IllegalArgumentException() }
        }

    private val tf = TypeFactory.defaultInstance()
    private val KProperty1<*, *>.jsonType
        get() = tf.constructType(javaGetter?.genericReturnType)!!

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): T {
        val codec = parser.codec!!
        require(parser.currentToken == JsonToken.START_OBJECT)

        var tag: String? = null
        var content: JsonNode? = null

        var result: T? = null

        var token: JsonToken = parser.nextValue()

        while(token != JsonToken.END_OBJECT) {
            val field = parser.currentName
            when (field) {
                "t" -> {
                    require(parser.currentToken == JsonToken.VALUE_STRING)
                    tag = parser.text!!
                    if (content != null) {
                        result = readClassContents(tag, codec.treeAsTokens(content))
                    }
                }
                "c" -> {
                    when (tag) {
                        null -> {
                            content = codec.readTree(parser)
                        }
                        else -> {
                            result = readClassContents(tag, parser)
                        }
                    }
                }
            }
            token = parser.nextValue()
        }

        // there is tag, but no content => it's probably a property-less object, just try parsing it as-is
        if (result == null && tag != null) {
            when {
                clazz.java.isEnum -> result = readEnumContents(tag)
            // there is nothing left to read in parser, but this should not read anything anyway
                else -> result = readClassContents(tag, parser)
            }
        }

        result ?: throw IllegalArgumentException()
        return result
    }

    private fun readEnumContents(tag: String): T =
            requireNotNull(clazz.java.enumConstants.find { "$it" == tag })

    private fun readClassContents(tag: String, parser: JsonParser): T {
        val codec = parser.codec
        val clazz = checkNotNull(successors[tag])
        val props = clazz.sortedProperties
        return when(props.size) {
            0 -> requireNotNull(clazz.objectInstance)
            1 -> {
                val value: Any? = codec.readValue(parser, props.first().jsonType)
                clazz.primaryConstructor!!.call(value)
            }
            else -> {
                require(parser.currentToken == JsonToken.START_ARRAY)
                parser.nextToken()
                val cc = props.map { prop ->
                    codec.readValue<Any>(parser, prop.jsonType)
                }
                require(parser.nextToken() == JsonToken.END_ARRAY)

                clazz.primaryConstructor!!.call(*(cc.toTypedArray()))
            }
        }
    }

}
