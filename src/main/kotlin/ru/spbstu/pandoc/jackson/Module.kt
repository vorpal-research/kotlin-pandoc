package ru.spbstu.pandoc.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ru.spbstu.ktuples.jackson.KTuplesModule
import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.CitationMode
import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.MetaValue
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL

fun constructObjectMapper(): ObjectMapper {
    val module: Module = SimpleModule()
            .addSerializer(PandocSerializer(Inline::class))
            .addSerializer(PandocSerializer(Block::class))
            .addSerializer(PandocSerializer(MetaValue::class))
            .addSerializer(PandocSerializer(CitationMode::class))
            .addDeserializer(Inline::class.java, PandocDeserializer(Inline::class))
            .addDeserializer(Block::class.java, PandocDeserializer(Block::class))
            .addDeserializer(MetaValue::class.java, PandocDeserializer(MetaValue::class))
            .addDeserializer(CitationMode::class.java, PandocDeserializer(CitationMode::class))

    return ObjectMapper().registerModule(KotlinModule())
            .registerModule(KTuplesModule())
            .registerModule(module)
}

inline fun <reified T: Any> typeRef() = object : TypeReference<T>(){}
inline fun <reified T: Any> ObjectMapper.readValue(cx: String) = readValue<T>(cx, typeRef<T>())!!
inline fun <reified T: Any> ObjectMapper.readValue(cx: URL) = readValue<T>(cx, typeRef<T>())!!
inline fun <reified T: Any> ObjectMapper.readValue(cx: File) = readValue<T>(cx, typeRef<T>())!!
inline fun <reified T: Any> ObjectMapper.readValue(cx: InputStream) = readValue<T>(cx, typeRef<T>())!!
inline fun <reified T: Any> ObjectMapper.readValue(cx: Reader) = readValue<T>(cx, typeRef<T>())!!
inline fun <reified T: Any> ObjectMapper.readValue(cx: ByteArray) = readValue<T>(cx, typeRef<T>())!!
