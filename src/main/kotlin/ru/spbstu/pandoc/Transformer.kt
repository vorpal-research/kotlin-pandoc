package ru.spbstu.pandoc

import com.fasterxml.jackson.module.kotlin.readValue
import ru.spbstu.pandoc.jackson.constructObjectMapper
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer

open class PandocVisitor {
    open fun visit(v: MetaValue): MetaValue = v.accept(this)
    open fun visit(v: MetaValue.MetaMap): MetaValue = v.accept(this)
    open fun visit(v: MetaValue.MetaList): MetaValue = v.accept(this)
    open fun visit(v: MetaValue.MetaBool): MetaValue = v.accept(this)
    open fun visit(v: MetaValue.MetaString): MetaValue = v.accept(this)
    open fun visit(v: MetaValue.MetaInlines): MetaValue = v.accept(this)
    open fun visit(v: MetaValue.MetaBlocks): MetaValue = v.accept(this)

    open fun visit(b: Block): Block = b.accept(this)
    open fun visit(b: Block.Plain): Block = b.accept(this)
    open fun visit(b: Block.Para): Block = b.accept(this)
    open fun visit(b: Block.LineBlock): Block = b.accept(this)
    open fun visit(b: Block.CodeBlock): Block = b.accept(this)
    open fun visit(b: Block.RawBlock): Block = b.accept(this)
    open fun visit(b: Block.BlockQuote): Block = b.accept(this)
    open fun visit(b: Block.OrderedList): Block = b.accept(this)
    open fun visit(b: Block.BulletList): Block = b.accept(this)
    open fun visit(b: Block.DefinitionList): Block = b.accept(this)
    open fun visit(b: Block.Header): Block = b.accept(this)
    open fun visit(b: Block.HorizontalRule): Block = b.accept(this)
    open fun visit(b: Block.Table): Block = b.accept(this)
    open fun visit(b: Block.Div): Block = b.accept(this)
    open fun visit(b: Block.Null): Block = b.accept(this)

    open fun visit(bs: List<Block>, token: Block? = null) = bs.accept(this)

    open fun visit(di: DefinitionItem): DefinitionItem = di.accept(this)

    open fun visit(i: Inline): Inline = i.accept(this)
    open fun visit(i: Inline.Str): Inline = i.accept(this)
    open fun visit(i: Inline.Emph): Inline = i.accept(this)
    open fun visit(i: Inline.Strong): Inline = i.accept(this)
    open fun visit(i: Inline.Strikeout): Inline = i.accept(this)
    open fun visit(i: Inline.Superscript): Inline = i.accept(this)
    open fun visit(i: Inline.Subscript): Inline = i.accept(this)
    open fun visit(i: Inline.SmallCaps): Inline = i.accept(this)
    open fun visit(i: Inline.Quoted): Inline = i.accept(this)
    open fun visit(i: Inline.Cite): Inline = i.accept(this)
    open fun visit(i: Inline.Code): Inline = i.accept(this)
    open fun visit(i: Inline.Space): Inline = i.accept(this)
    open fun visit(i: Inline.SoftBreak): Inline = i.accept(this)
    open fun visit(i: Inline.LineBreak): Inline = i.accept(this)
    open fun visit(i: Inline.Math): Inline = i.accept(this)
    open fun visit(i: Inline.RawInline): Inline = i.accept(this)
    open fun visit(i: Inline.Link): Inline = i.accept(this)
    open fun visit(i: Inline.Image): Inline = i.accept(this)
    open fun visit(i: Inline.Note): Inline = i.accept(this)
    open fun visit(i: Inline.Span): Inline = i.accept(this)

    open fun visit(iis: List<Inline>, token: Inline? = null) = iis.accept(this)

    open fun visit(c: Citation): Citation = c.accept(this)

    open fun visit(doc: Pandoc): Pandoc = doc.accept(this)
}

fun MetaValue.accept(visitor: PandocVisitor): MetaValue {
    return when(this) {
        is MetaValue.MetaMap -> visitor.visit(this)
        is MetaValue.MetaList -> visitor.visit(this)
        is MetaValue.MetaBool -> visitor.visit(this)
        is MetaValue.MetaString -> visitor.visit(this)
        is MetaValue.MetaInlines -> visitor.visit(this)
        is MetaValue.MetaBlocks -> visitor.visit(this)
    }
}
fun MetaValue.MetaMap.accept(visitor: PandocVisitor) =
        copy(map = map.mapValues { (_, v) -> visitor.visit(v) })
fun MetaValue.MetaList.accept(visitor: PandocVisitor) =
        copy(list = list.map { visitor.visit(it) })
fun MetaValue.MetaBool.accept(visitor: PandocVisitor) = this
fun MetaValue.MetaString.accept(visitor: PandocVisitor) = this
fun MetaValue.MetaInlines.accept(visitor: PandocVisitor) = copy(value = visitor.visit(value))
fun MetaValue.MetaBlocks.accept(visitor: PandocVisitor) = copy(value = visitor.visit(value))

@JvmName("acceptBlocks")
fun List<Block>.accept(visitor: PandocVisitor): List<Block> =
        map { visitor.visit(it) }
fun Block.accept(visitor: PandocVisitor): Block {
    return when(this) {
        is Block.Plain -> visitor.visit(this)
        is Block.Para -> visitor.visit(this)
        is Block.LineBlock -> visitor.visit(this)
        is Block.CodeBlock -> visitor.visit(this)
        is Block.RawBlock -> visitor.visit(this)
        is Block.BlockQuote -> visitor.visit(this)
        is Block.OrderedList -> visitor.visit(this)
        is Block.BulletList -> visitor.visit(this)
        is Block.DefinitionList -> visitor.visit(this)
        is Block.Header -> visitor.visit(this)
        is Block.HorizontalRule -> visitor.visit(this)
        is Block.Table -> visitor.visit(this)
        is Block.Div -> visitor.visit(this)
        is Block.Null -> visitor.visit(this)
    }
}

fun Block.Plain.accept(visitor: PandocVisitor): Block = copy(inlines = visitor.visit(inlines))
fun Block.Para.accept(visitor: PandocVisitor): Block = copy(inlines = visitor.visit(inlines))
fun Block.LineBlock.accept(visitor: PandocVisitor): Block = copy(inlines = inlines.map { visitor.visit(it) })
fun Block.CodeBlock.accept(visitor: PandocVisitor): Block = this
fun Block.RawBlock.accept(visitor: PandocVisitor): Block = this
fun Block.BlockQuote.accept(visitor: PandocVisitor): Block = copy(blocks = visitor.visit(blocks))
fun Block.OrderedList.accept(visitor: PandocVisitor): Block = copy(items = items.map { visitor.visit(it) })
fun Block.BulletList.accept(visitor: PandocVisitor): Block = copy(items = items.map { visitor.visit(it) })
fun Block.DefinitionList.accept(visitor: PandocVisitor): Block = copy(items = items.map { visitor.visit(it) })
fun Block.Header.accept(visitor: PandocVisitor): Block = copy(text = visitor.visit(text))
fun Block.HorizontalRule.accept(visitor: PandocVisitor): Block = this
fun Block.Table.accept(visitor: PandocVisitor): Block =
        copy(
                caption = caption.accept(visitor),
                columnHeaders = columnHeaders.map { visitor.visit(it) },
                rows = rows.map { it.map { visitor.visit(it) }}
        )
fun Block.Div.accept(visitor: PandocVisitor): Block = copy(contents = visitor.visit(contents))
fun Block.Null.accept(visitor: PandocVisitor): Block = this

fun DefinitionItem.accept(visitor: PandocVisitor): DefinitionItem =
        copy(
                v0 = visitor.visit(v0),
                v1 = v1.map { visitor.visit(it) }
        )

@JvmName("acceptInlines")
fun List<Inline>.accept(visitor: PandocVisitor): List<Inline> =
        map { visitor.visit(it) }
fun Inline.accept(visitor: PandocVisitor): Inline {
    return when(this) {
        is Inline.Str -> visitor.visit(this)
        is Inline.Emph -> visitor.visit(this)
        is Inline.Strong -> visitor.visit(this)
        is Inline.Strikeout -> visitor.visit(this)
        is Inline.Superscript -> visitor.visit(this)
        is Inline.Subscript -> visitor.visit(this)
        is Inline.SmallCaps -> visitor.visit(this)
        is Inline.Quoted -> visitor.visit(this)
        is Inline.Cite -> visitor.visit(this)
        is Inline.Code -> visitor.visit(this)
        is Inline.Space -> visitor.visit(this)
        is Inline.SoftBreak -> visitor.visit(this)
        is Inline.LineBreak -> visitor.visit(this)
        is Inline.Math -> visitor.visit(this)
        is Inline.RawInline -> visitor.visit(this)
        is Inline.Link -> visitor.visit(this)
        is Inline.Image -> visitor.visit(this)
        is Inline.Note -> visitor.visit(this)
        is Inline.Span -> visitor.visit(this)
    }
}
fun Inline.Str.accept(visitor: PandocVisitor): Inline = this
fun Inline.Emph.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Strong.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Strikeout.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Superscript.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Subscript.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.SmallCaps.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Quoted.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Cite.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents), citation = citation.accept(visitor))
fun Inline.Code.accept(visitor: PandocVisitor): Inline = this
fun Inline.Space.accept(visitor: PandocVisitor): Inline = this
fun Inline.SoftBreak.accept(visitor: PandocVisitor): Inline = this
fun Inline.LineBreak.accept(visitor: PandocVisitor): Inline = this
fun Inline.Math.accept(visitor: PandocVisitor): Inline = this
fun Inline.RawInline.accept(visitor: PandocVisitor): Inline = this
fun Inline.Link.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))
fun Inline.Image.accept(visitor: PandocVisitor): Inline = copy(altText = visitor.visit(altText))
fun Inline.Note.accept(visitor: PandocVisitor): Inline = copy(blocks = visitor.visit(blocks))
fun Inline.Span.accept(visitor: PandocVisitor): Inline = copy(contents = visitor.visit(contents))

fun List<Citation>.accept(visitor: PandocVisitor): List<Citation> = map { visitor.visit(it) }
fun Citation.accept(visitor: PandocVisitor): Citation =
        copy(citationPrefix = visitor.visit(citationPrefix),
                citationSuffix = visitor.visit(citationSuffix))

fun Pandoc.accept(visitor: PandocVisitor): Pandoc =
        copy(
           meta = meta.mapValues { (_, v) -> visitor.visit(v) },
           blocks = visitor.visit(blocks)
        )

fun makeFilter(
        visitor: PandocVisitor = PandocVisitor(),
        input: Reader = InputStreamReader(System.`in`),
        output: Writer = OutputStreamWriter(System.out),
        needsClosing: Boolean = false
) {
    val om = constructObjectMapper()
    val ii = om.readValue<Pandoc>(input)
    val oo = visitor.visit(ii)
    om.writeValue(output, oo)
    if(needsClosing) {
        input.close()
        output.close()
    }
}
