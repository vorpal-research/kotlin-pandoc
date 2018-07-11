package ru.spbstu.pandoc.builder

import ru.spbstu.ktuples.Tuple2
import ru.spbstu.pandoc.Attr
import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.Format
import ru.spbstu.pandoc.ListAttributes

@BuilderMarker
sealed class BlockBuilderBase {
    private val list: MutableList<Block> = mutableListOf()
    fun build(): List<Block> = list

    fun plain(body: InlineBuilder.() -> Unit) {
        list += Block.Plain(inlines(body))
    }
    fun para(body: InlineBuilder.() -> Unit) {
        list += Block.Para(inlines(body))
    }
    fun lineBlock(vararg lines: InlineBuilder.() -> Unit) {
        val res = lines.map { inlines(it) }
        list += Block.LineBlock(res)
    }
    fun codeBlock(attr: Attr = Attr(), codeBody: () -> String) {
        list += Block.CodeBlock(attr = Attr(), text = codeBody())
    }
    fun rawBlock(format: Format, text: () -> String) {
        list += Block.RawBlock(format, text())
    }
    fun blockQuote(body: BlockBuilder.() -> Unit) {
        list += Block.BlockQuote(blocks(body))
    }
    fun orderedList(attributes: ListAttributes = ListAttributes(), items: ListBuilder.() -> Unit)  {
        val builder = ListBuilder()
        builder.items()
        list += Block.OrderedList(attributes, builder.build())
    }
    fun bulletList(items: ListBuilder.() -> Unit) {
        val builder = ListBuilder()
        builder.items()
        list += Block.BulletList(builder.build())
    }
    fun definitionList(body: DefinitionListBuilder.() -> Unit) {
        val builder = DefinitionListBuilder()
        builder.body()
        list += Block.DefinitionList(builder.build())
    }
    fun header(level: Int, text: InlineBuilderWithAttrs.() -> Unit) {
        val builderWithAttrs = InlineBuilderWithAttrs()
        builderWithAttrs.text()
        list += Block.Header(level, builderWithAttrs.buildAttrs(), builderWithAttrs.build())
    }
    fun hrule() {
        list += Block.HorizontalRule
    }
    fun table(body: TableBuilder.() -> Unit) {
        val builder = TableBuilder()
        builder.body()
        list += builder.build()
    }
    fun div(body: BlockBuilderWithAtts.() -> Unit) {
        val builder = BlockBuilderWithAtts()
        builder.body()
        list += Block.Div(builder.buildAttrs(), builder.build())
    }
    fun nul() {
        list += Block.Null
    }

}

class BlockBuilder: BlockBuilderBase() {}

class BlockBuilderWithAtts: BlockBuilderBase() {
    var id: String = ""
    var classes: List<String> = listOf()
    var clazz: String get() = classes.first(); set(value) { classes = listOf(value) }
    var properties: List<Tuple2<String, String>> = listOf()

    fun buildAttrs(): Attr = Attr(id, classes, properties)
}

fun blocks(body: BlockBuilder.() -> Unit): List<Block> {
    val builder = BlockBuilder()
    builder.body()
    return builder.build()
}
