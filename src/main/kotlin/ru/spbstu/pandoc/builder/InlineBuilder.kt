package ru.spbstu.pandoc.builder

import ru.spbstu.ktuples.Tuple2
import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.Target

@BuilderMarker
sealed class InlineBuilderBase {
    private val list: MutableList<Inline> = mutableListOf()
    fun build(): List<Inline> = list

    operator fun String.unaryPlus() {
        list += Inline.Str(this)
    }
    operator fun Inline.unaryPlus() { list.add(this) }
    operator fun Iterable<Inline>.unaryPlus() { list.addAll(this) }
    operator fun Sequence<Inline>.unaryPlus() { list.addAll(this) }

    fun str(text: () -> String) {
        list += Inline.Str(text())
    }
    fun emph(body: InlineBuilder.() -> Unit) {
        list += Inline.Emph(inlines(body))
    }
    fun strong(body: InlineBuilder.() -> Unit) {
        list += Inline.Strong(inlines(body))
    }
    fun strikeout(body: InlineBuilder.() -> Unit) {
        list += Inline.Strikeout(inlines(body))
    }
    fun superscript(body: InlineBuilder.() -> Unit) {
        list += Inline.Superscript(inlines(body))
    }
    fun subscript(body: InlineBuilder.() -> Unit) {
        list += Inline.Subscript(inlines(body))
    }
    fun smallCaps(body: InlineBuilder.() -> Unit) {
        list += Inline.SmallCaps(inlines(body))
    }
    fun quoted(type: QuoteType = QuoteType.DoubleQuote, body: InlineBuilder.() -> Unit) {
        list += Inline.Quoted(type, inlines(body))
    }
    fun cite() {
        list += TODO() as Inline.Cite
    }
    fun code(text: AttributeBuilder.() -> String) {
        val builder = AttributeBuilder()
        val source = builder.text()
        list += Inline.Code(builder.buildAttrs(), source)
    }
    fun space() {
        list += Inline.Space
    }
    fun softBreak() {
        list += Inline.SoftBreak
    }
    fun lineBreak() {
        list += Inline.LineBreak
    }
    fun math(type: MathType = MathType.InlineMath, text: () -> String) {
        list += Inline.Math(type, text())
    }
    fun rawInline(format: Format, text: () -> String) {
        list += Inline.RawInline(format, text())
    }
    fun link(target: Target, contents: InlineBuilderWithAttrs.() -> Unit = {}) {
        val builder = InlineBuilderWithAttrs()
        builder.contents()
        list += Inline.Link(builder.buildAttrs(), builder.build(), target)
    }
    fun image(target: Target, altText: InlineBuilderWithAttrs.() -> Unit = {}) {
        val builder = InlineBuilderWithAttrs()
        builder.altText()
        list += Inline.Image(builder.buildAttrs(), builder.build(), target)
    }
    fun note(body: BlockBuilder.() -> Unit) {
        list += Inline.Note(blocks(body))
    }
    fun span(contents: InlineBuilderWithAttrs.() -> Unit) {
        val builder = InlineBuilderWithAttrs()
        builder.contents()
        list += Inline.Span(builder.buildAttrs(), builder.build())
    }

}

class InlineBuilder: InlineBuilderBase()
class InlineBuilderWithAttrs: InlineBuilderBase(), AttributeBuilder by AttributeBuilder()

fun inlines(body: InlineBuilder.() -> Unit): List<Inline> {
    val builder = InlineBuilder()
    builder.body()
    return builder.build()
}

fun inline(body: InlineBuilder.() -> Unit): Inline = inlines(body).single()
