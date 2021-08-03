package ru.spbstu.pandoc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.spbstu.ktuples.Tuple2
import ru.spbstu.pandoc.kxserialization.*

@Serializable
data class Pandoc constructor(
        val blocks: List<Block>,
        @SerialName("pandoc-api-version")
        val apiVersion: List<Int>,
        val meta: Map<String, MetaValue>
)
@Serializable(with = MetaValueSerializer::class)
sealed class MetaValue {
    @Serializable
    data class MetaMap(val map: Map<String, MetaValue>): MetaValue()
    @Serializable
    data class MetaList(val list: List<MetaValue>): MetaValue()
    @Serializable
    data class MetaBool(val value: Boolean): MetaValue()
    @Serializable
    data class MetaString(val value: String): MetaValue()
    @Serializable
    data class MetaInlines(val value: List<Inline>): MetaValue()
    @Serializable
    data class MetaBlocks(val value: List<Block>): MetaValue()
}

private fun MetaValue.extractInlines(): List<Inline>? = when(this) {
    is MetaValue.MetaInlines -> value
    is MetaValue.MetaString -> listOf(Inline.Str(value))
    is MetaValue.MetaBlocks -> when(val firstBlock = value.singleOrNull()) {
        is Block.Plain -> firstBlock.inlines
        is Block.Para -> firstBlock.inlines
        else -> null
    }
    else -> null
}

val Pandoc.title: List<Inline>?
    get(): List<Inline>? = meta["title"]?.extractInlines()

val Pandoc.date: List<Inline>?
    get(): List<Inline>? = meta["date"]?.extractInlines()

val Pandoc.authors: List<List<Inline>>?
    get(): List<List<Inline>>? {
        val metaAuthor = meta["author"]
        return when(metaAuthor) {
            is MetaValue.MetaList -> metaAuthor.list.mapNotNull { it.extractInlines() }
            else -> metaAuthor?.extractInlines()?.let { listOf(it) }
        }
    }

interface Attributes {
    val attr: Attr
}

@Serializable(with = BlockSerializer::class)
sealed class Block {
    override fun toString() = this::class.simpleName!!

    @Serializable
    data class Plain(val inlines: List<Inline>): Block()
    @Serializable
    data class Para(val inlines: List<Inline>): Block()
    @Serializable
    data class LineBlock(val inlines: List<List<Inline>>): Block()
    @Serializable
    data class CodeBlock(override val attr: Attr, val text: String): Block(), Attributes
    @Serializable
    data class RawBlock(val format: Format, val text: String): Block()
    @Serializable
    data class BlockQuote(val blocks: List<Block>): Block()
    @Serializable
    data class OrderedList(val attributes: ListAttributes, val items: List<List<Block>>): Block()
    @Serializable
    data class BulletList(val items: List<List<Block>>): Block()
    @Serializable
    data class DefinitionList(val items: List<DefinitionItem>): Block()
    @Serializable
    data class Header(val level: Int, override val attr: Attr, val text: List<Inline>): Block(), Attributes
    @Serializable
    object HorizontalRule: Block()
    @Serializable
    data class Table(
            val caption: List<Inline>,
            val alignments: List<Alignment>,
            val widths: List<Double>,
            val columnHeaders: List<TableCell>,
            val rows: List<List<TableCell>>
    ): Block()
    @Serializable
    data class Div(override val attr: Attr, val contents: List<Block>): Block(), Attributes
    @Serializable
    object Null: Block()
}

typealias DefinitionItem = Tuple2<List<Inline>, List<List<Block>>>

@Serializable(with = InlineSerializer::class)
sealed class Inline {
    override fun toString() = this::class.simpleName!!

    @Serializable
    data class Str(val text: String): Inline()
    @Serializable
    data class Emph(val contents: List<Inline>): Inline()
    @Serializable
    data class Strong(val contents: List<Inline>): Inline()
    @Serializable
    data class Strikeout(val contents: List<Inline>): Inline()
    @Serializable
    data class Superscript(val contents: List<Inline>): Inline()
    @Serializable
    data class Subscript(val contents: List<Inline>): Inline()
    @Serializable
    data class SmallCaps(val contents: List<Inline>): Inline()
    @Serializable
    data class Quoted(val type: QuoteType, val contents: List<Inline>): Inline()
    @Serializable
    data class Cite(val citation: List<Citation>, val contents: List<Inline>): Inline()
    @Serializable
    data class Code(override val attr: Attr, val text: String): Inline(), Attributes
    @Serializable
    object Space: Inline()
    @Serializable
    object SoftBreak: Inline()
    @Serializable
    object LineBreak: Inline()
    @Serializable
    data class Math(val type: MathType, val text: String): Inline()
    @Serializable
    data class RawInline(val format: Format, val text: String): Inline()
    @Serializable
    data class Link(override val attr: Attr, val contents: List<Inline>, val target: Target): Inline(), Attributes
    @Serializable
    data class Image(override val attr: Attr, val altText: List<Inline>, val target: Target): Inline(), Attributes
    @Serializable
    data class Note(val blocks: List<Block>): Inline()
    @Serializable
    data class Span(override val attr: Attr, val contents: List<Inline>): Inline(), Attributes
}

@Serializable(with = AlignmentSerializer::class)
enum class Alignment{ AlignLeft, AlignRight, AlignCenter, AlignDefault }
@Serializable(with = ListAttributesSerializer::class)
data class ListAttributes(
    val level: Int = 0,
    val numberStyle: ListNumberStyle = ListNumberStyle.DefaultStyle,
    val numberDelim: ListNumberDelim = ListNumberDelim.DefaultDelim
)
@Serializable(with = ListNumberStyleSerializer::class)
enum class ListNumberStyle{ DefaultStyle, Example, Decimal, LowerRoman, UpperRoman, LowerAlpha, UpperAlpha }
@Serializable(with = ListNumberDelimSerializer::class)
enum class ListNumberDelim{ DefaultDelim, Period, OneParen, TwoParens }
@JvmInline
@Serializable
value class Format(val format: String) {
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
@Serializable(with = AttrSerializer::class)
data class Attr(
        val id: String = "",
        val classes: List<String> = listOf(),
        val properties: List<Tuple2<String, String>> = listOf()
) {
    val propertiesMap by lazy { properties.associate { it.v0 to it.v1 } }
}
typealias TableCell = List<Block>
@Serializable(with = QuoteTypeSerializer::class)
enum class QuoteType{ SingleQuote, DoubleQuote }
typealias Target = Tuple2<String,String>
fun Target(to: String) = Target(to, to)
@Serializable(with = MathTypeSerializer::class)
enum class MathType { DisplayMath, InlineMath }
@Serializable
data class Citation(
        val citationId: String,
        val citationPrefix: List<Inline>,
        val citationSuffix: List<Inline>,
        val citationMode: CitationMode,
        val citationNoteNum: Int,
        val citationHash: Int
)
@Serializable(with = CitationModeSerializer::class)
enum class CitationMode { AuthorInText, SuppressAuthor, NormalCitation }
