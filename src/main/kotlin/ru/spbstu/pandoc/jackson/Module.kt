package ru.spbstu.pandoc.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ru.spbstu.ktuples.jackson.KTuplesModule
import ru.spbstu.pandoc.*
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL

fun constructObjectMapper(): ObjectMapper {
    val classes = listOf(
            Inline::class,
            Block::class,
            MetaValue::class,
            CitationMode::class,
            MathType::class,
            QuoteType::class,
            ListNumberStyle::class,
            ListNumberDelim::class,
            Alignment::class
    )

    val module: SimpleModule = SimpleModule()
    classes.forEach {
        module.addSerializer(PandocSerializer(it))
                .addDeserializer(it.java as Class<Any>, PandocDeserializer(it))
    }

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
