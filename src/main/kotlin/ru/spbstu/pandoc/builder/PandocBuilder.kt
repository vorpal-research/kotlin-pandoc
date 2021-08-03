package ru.spbstu.pandoc.builder

import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.Format
import ru.spbstu.pandoc.MetaValue
import ru.spbstu.pandoc.Pandoc
import java.io.File

class PandocBuilder {
    private var blocks: List<Block> = listOf()
    var apiVersion: List<Int> = listOf(1, 17, 5, 1)
    val meta: MutableMap<String, MetaValue> = mutableMapOf()

    fun body(constructor: BlockBuilder.() -> Unit) {
        val builder = BlockBuilder()
        builder.constructor()
        blocks = builder.build()
    }

    fun title(constructor: InlineBuilder.() -> Unit) {
        val builder = InlineBuilder()
        builder.constructor()
        meta["title"] = MetaValue.MetaInlines(builder.build())
    }

    fun date(constructor: InlineBuilder.() -> Unit) {
        val builder = InlineBuilder()
        builder.constructor()
        meta["date"] = MetaValue.MetaInlines(builder.build())
    }

    fun authors(constructor: InlineBuilder.() -> Unit) {
        val builder = InlineBuilder()
        builder.constructor()
        meta["author"] = MetaValue.MetaInlines(builder.build())
    }

    fun build(): Pandoc = Pandoc(blocks, apiVersion, meta)
}

fun pandoc(body: PandocBuilder.() -> Unit): Pandoc =
        PandocBuilder().apply(body).build()
