package ru.spbstu.pandoc

import com.fasterxml.jackson.annotation.*
import ru.spbstu.ktuples.Tuple2

data class Pandoc constructor(
        val blocks: List<Block>,
        @JsonProperty("pandoc-api-version")
        val apiVersion: List<Int>,
        val meta: Map<String, MetaValue>
)
sealed class MetaValue {
    data class MetaMap(val map: Map<String, MetaValue>): MetaValue()
    data class MetaList(val list: List<MetaValue>): MetaValue()
    data class MetaBool(val value: Boolean): MetaValue()
    data class MetaString(val value: String): MetaValue()
    data class MetaInlines(val value: List<Inline>): MetaValue()
    data class MetaBlocks(val value: List<Block>): MetaValue()
}

val Pandoc.title: List<Inline>?
    get(): List<Inline>? {
        val metaTitle = meta["title"]
        return when(metaTitle) {
            is MetaValue.MetaInlines -> metaTitle.value
            else -> null
        }
    }

val Pandoc.date: List<Inline>?
    get(): List<Inline>? {
        val metaDate = meta["date"]
        return when(metaDate) {
            is MetaValue.MetaInlines -> metaDate.value
            else -> null
        }
    }

val Pandoc.authors: List<List<Inline>>?
    get(): List<List<Inline>>? {
        val metaAuthor = meta["author"]
        return when(metaAuthor) {
            is MetaValue.MetaList -> metaAuthor.list.mapNotNull {
                when(it) {
                    is MetaValue.MetaInlines -> it.value
                    else -> null
                }
            }
            is MetaValue.MetaInlines -> listOf(metaAuthor.value)
            else -> null
        }
    }

interface Attributes {
    val attr: Attr
}

sealed class Block {
    override fun toString() = this::class.simpleName!!

    data class Plain(val inlines: List<Inline>): Block()
    data class Para(val inlines: List<Inline>): Block()
    data class LineBlock(val inlines: List<List<Inline>>): Block()
    data class CodeBlock(override val attr: Attr, val text: String): Block(), Attributes
    data class RawBlock(val format: Format, val text: String): Block()
    data class BlockQuote(val blocks: List<Block>): Block()
    data class OrderedList(val attributes: ListAttributes, val items: List<List<Block>>): Block()
    data class BulletList(val items: List<List<Block>>): Block()
    data class DefinitionList(val items: List<DefinitionItem>): Block()
    data class Header(val level: Int, override val attr: Attr, val text: List<Inline>): Block(), Attributes
    object HorizontalRule: Block()
    data class Table(
            val caption: List<Inline>,
            val alignments: List<Alignment>,
            val widths: List<Double>,
            val columnHeaders: List<TableCell>,
            val rows: List<List<TableCell>>
    ): Block()
    data class Div(override val attr: Attr, val contents: List<Block>): Block(), Attributes
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
    data class Code(override val attr: Attr, val text: String): Inline(), Attributes
    object Space: Inline()
    object SoftBreak: Inline()
    object LineBreak: Inline()
    data class Math(val type: MathType, val text: String): Inline()
    data class RawInline(val format: Format, val text: String): Inline()
    data class Link(override val attr: Attr, val contents: List<Inline>, val target: Target): Inline(), Attributes
    data class Image(override val attr: Attr, val altText: List<Inline>, val target: Target): Inline(), Attributes
    data class Note(val blocks: List<Block>): Inline()
    data class Span(override val attr: Attr, val contents: List<Inline>): Inline(), Attributes
}

enum class Alignment{ AlignLeft, AlignRight, AlignCenter, AlignDefault }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class ListAttributes(
    val level: Int = 0,
    val numberStyle: ListNumberStyle = ListNumberStyle.DefaultStyle,
    val numberDelim: ListNumberDelim = ListNumberDelim.DefaultDelim
)
enum class ListNumberStyle{ DefaultStyle, Example, Decimal, LowerRoman, UpperRoman, LowerAlpha, UpperAlpha }
enum class ListNumberDelim{ DefaultDelim, Period, OneParen, TwoParens }
data class Format @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val format: String) {
    companion object {
        val supportedFormats = setOf(
            "asciidoc",
            "beamer",
            "commonmark",
            "context",
            "docbook",
            "docbook4",
            "docbook5",
            "docx",
            "dokuwiki",
            "dzslides",
            "epub",
            "epub2",
            "epub3",
            "fb2",
            "gfm",
            "haddock",
            "html",
            "html4",
            "html5",
            "icml",
            "jats",
            "json",
            "latex",
            "man",
            "markdown",
            "markdown_github",
            "markdown_mmd",
            "markdown_phpextra",
            "markdown_strict",
            "mediawiki",
            "ms",
            "muse",
            "native",
            "odt",
            "opendocument",
            "opml",
            "org",
            "plain",
            "pptx",
            "revealjs",
            "rst",
            "rtf",
            "s5",
            "slideous",
            "slidy",
            "tei",
            "texinfo",
            "textile",
            "zimwiki",
            "tex" // not really a supported format, but used in raw blocks/inlines
        )
        val htmlFormats = setOf("html", "html4", "html5", "revealjs", "s5", "slideous", "slidy", "epub", "epub2", "epub3")
        val latexFormats = setOf("latex", "beamer")
        val texFormats = latexFormats + "context"

        val HTML = Format("html")
        val TeX = Format("tex")
    }

    init {
        check(format in supportedFormats) { "Format unsupported by pandoc: \"$format\"" }
    }

    fun isHTML() = format in htmlFormats
    fun isLaTeX() = format in latexFormats
    fun isTeX() = format in texFormats
}
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class Attr(
        val id: String = "",
        val classes: List<String> = listOf(),
        val properties: List<Tuple2<String, String>> = listOf()
) {
    @get:JsonIgnore
    val propertiesMap by lazy { properties.associate { it.v0 to it.v1 } }
}
typealias TableCell = List<Block>
enum class QuoteType{ SingleQuote, DoubleQuote }
typealias Target = Tuple2<String,String>
fun Target(to: String) = Target(to, to)
enum class MathType { DisplayMath, InlineMath }
data class Citation(
        val citationId: String,
        val citationPrefix: List<Inline>,
        val citationSuffix: List<Inline>,
        val citationMode: CitationMode,
        val citationNoteNum: Int,
        val citationHash: Int
)
enum class CitationMode { AuthorInText, SuppressAuthor, NormalCitation }
