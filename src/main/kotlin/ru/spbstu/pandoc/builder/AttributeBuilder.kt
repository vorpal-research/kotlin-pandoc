package ru.spbstu.pandoc.builder

import ru.spbstu.ktuples.Tuple2
import ru.spbstu.pandoc.Attr

interface AttributeBuilder {
    var id: String
    val classes: MutableList<String>
    var clazz: String get() = classes.first(); set(value) { classes.add(value) }
    val properties: MutableList<Tuple2<String, String>>

    fun buildAttrs(): Attr = Attr(id, classes, properties)
}

fun AttributeBuilder() = object : AttributeBuilder {
    override var id: String = ""
    override val classes: MutableList<String> = mutableListOf()
    override val properties: MutableList<Tuple2<String, String>> = mutableListOf()
}
