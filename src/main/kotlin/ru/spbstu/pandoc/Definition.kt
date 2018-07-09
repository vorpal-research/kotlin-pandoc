package ru.spbstu.pandoc

import com.fasterxml.jackson.annotation.JsonProperty
import ru.spbstu.ktuples.Tuple2
import ru.spbstu.ktuples.Tuple3

interface ToString {
    override fun toString(): String
}
class DataObject : ToString {
    override fun toString(): String = this::class.simpleName!!
}

data class Pandoc constructor(
        val meta: Map<String, MetaValue>,
        val blocks: List<Block>,
        @JsonProperty("pandoc-api-version")
        val apiVersion: List<Int>
)
sealed class MetaValue {
    data class MetaMap(val map: Map<String, MetaValue>): MetaValue()
    data class MetaList(val list: List<MetaValue>): MetaValue()
    data class MetaBool(val value: Boolean): MetaValue()
    data class MetaString(val value: String): MetaValue()
    data class MetaInlines(val value: List<Inline>): MetaValue()
    data class MetaBlocks(val value: List<Block>): MetaValue()
}

sealed class Block {
    override fun toString() = this::class.simpleName!!

    data class Plain(val inlines: List<Inline>): Block()
    data class Para(val inlines: List<Inline>): Block()
    data class LineBlock(val inlines: List<List<Inline>>): Block()
    data class CodeBlock(val attr: Attr, val text: String): Block()
    data class RawBlock(val format: Format, val text: String): Block()
    data class BlockQuote(val blocks: List<Block>): Block()
    data class OrderedList(val attributes: ListAttributes, val items: List<List<Block>>): Block()
    data class BulletList(val items: List<List<Block>>): Block()
    data class DefinitionList(val items: List<DefinitionItem>): Block()
    data class Header(val level: Int, val attr: Attr, val text: List<Inline>): Block()
    object HorizontalRule: Block()
    data class Table(
            val caption: List<Inline>,
            val alignments: List<Alignment>,
            val widths: List<Double>,
            val columnHeaders: List<TableCell>,
            val rows: List<List<TableCell>>
    ): Block()
    data class Div(val attr: Attr, val contents: List<Block>): Block()
    object Null: Block()
}

typealias DefinitionItem = Tuple2<List<Inline>, List<List<Block>>>

sealed class Inline {
    override fun toString() = this::class.simpleName!!

    data class Str(val text: String): Inline()
    data class Emph(val contents: List<Inline>): Inline()
    data class Strong(val contents: List<Inline>): Inline()
    data class Strikeout(val contents: List<Inline>): Inline()
    data class Superscript(val contents: List<Inline>): Inline()
    data class Subscript(val contents: List<Inline>): Inline()
    data class SmallCaps(val contents: List<Inline>): Inline()
    data class Quoted(val type: QuoteType, val contents: List<Inline>): Inline()
    data class Cite(val citation: List<Citation>, val contents: List<Inline>): Inline()
    data class Code(val attr: Attr, val text: String): Inline()
    object Space: Inline()
    object SoftBreak: Inline()
    object LineBreak: Inline()
    data class Math(val type: MathType, val text: String): Inline()
    data class RawInline(val format: Format, val text: String): Inline()
    data class Link(val attr: Attr, val contents: List<Inline>, val target: Target): Inline()
    data class Image(val attr: Attr, val altText: List<Inline>, val target: Target): Inline()
    data class Note(val blocks: List<Block>): Inline()
    data class Span(val attr: Attr, val contents: List<Inline>): Inline()
}


enum class Alignment{ AlignLeft, AlignRight, AlignCenter, AlignDefault }
typealias ListAttributes = Tuple3<Int, ListNumberStyle, ListNumberDelim>
enum class ListNumberStyle{ DefaultStyle, Example, Decimal, LowerRoman, UpperRoman, LowerAlpha, UpperAlpha }
enum class ListNumberDelim{ DefaultDelim, Period, OneParen, TwoParens }
data class Format(val format: String)
typealias Attr = Tuple3<String, List<String>, List<Pair<String, String>>>
typealias TableCell = List<Block>
enum class QuoteType{ SingleQuote, DoubleQuote }
typealias Target = Tuple2<String,String>
enum class MathType{ DisplayMath, InlineMath }
data class Citation constructor(
        val citationId: String,
        val citationPrefix: List<Inline>,
        val citationSuffix: List<Inline>,
        val citationMode: CitationMode,
        val citationNoteNum: Int,
        val citationHash: Int
)
sealed class CitationMode {
    override fun toString() = this::class.simpleName!!

    // this poor man's enum is here because that's how it works in pandoc
    object AuthorInText : CitationMode()
    object SuppressAuthor : CitationMode()
    object NormalCitation : CitationMode()
}
