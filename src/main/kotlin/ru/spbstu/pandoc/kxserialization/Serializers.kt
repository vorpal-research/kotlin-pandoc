@file: OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
package ru.spbstu.pandoc.kxserialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.StructureKind
import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.MetaValue.*
import ru.spbstu.pandoc.kxserialization.raw.UnwrappedDecoder
import ru.spbstu.pandoc.kxserialization.raw.UnwrappedEncoder

fun <T> AsPandocFieldSerializer(serializer: KSerializer<T>): KSerializer<T> {
    return when (serializer.descriptor.elementsCount) {
        0 -> serializer
        1 -> wrapSerializer(serializer, ::UnwrappedEncoder, ::UnwrappedDecoder) {
            lazyDescriptor(serialName = it.serialName) { it.getElementDescriptor(0) }
        }
        else -> wrapSerializer(
            serializer, { it }, { it }, { lazyDescriptor(kind = StructureKind.LIST) { it } }
        )
    }
}

private inline fun <reified T : Any> entry(crossinline serializer: () -> KSerializer<T>) = run {
    T::class to lazy { AsPandocFieldSerializer(serializer()) }
}

object MetaValueSerializer : KSerializer<MetaValue> by PolymorphicPandocishSerializer(
    entry { MetaMap.serializer() },
    entry { MetaList.serializer() },
    entry { MetaBool.serializer() },
    entry { MetaString.serializer() },
    entry { MetaInlines.serializer() },
    entry { MetaBlocks.serializer() },
)

object InlineSerializer : KSerializer<Inline> by PolymorphicPandocishSerializer(
    entry { Inline.Cite.serializer() },
    entry { Inline.Code.serializer() },
    entry { Inline.Emph.serializer() },
    entry { Inline.Image.serializer() },
    entry { Inline.LineBreak.serializer() },
    entry { Inline.Link.serializer() },
    entry { Inline.Math.serializer() },
    entry { Inline.Note.serializer() },
    entry { Inline.Quoted.serializer() },
    entry { Inline.RawInline.serializer() },
    entry { Inline.SmallCaps.serializer() },
    entry { Inline.SoftBreak.serializer() },
    entry { Inline.Space.serializer() },
    entry { Inline.Span.serializer() },
    entry { Inline.Str.serializer() },
    entry { Inline.Strikeout.serializer() },
    entry { Inline.Strong.serializer() },
    entry { Inline.Subscript.serializer() },
    entry { Inline.Superscript.serializer() },
)

object BlockSerializer : KSerializer<Block> by PolymorphicPandocishSerializer(
    entry { Block.BlockQuote.serializer() },
    entry { Block.BulletList.serializer() },
    entry { Block.CodeBlock.serializer() },
    entry { Block.DefinitionList.serializer() },
    entry { Block.Div.serializer() },
    entry { Block.Header.serializer() },
    entry { Block.HorizontalRule.serializer() },
    entry { Block.LineBlock.serializer() },
    entry { Block.Null.serializer() },
    entry { Block.OrderedList.serializer() },
    entry { Block.Para.serializer() },
    entry { Block.Plain.serializer() },
    entry { Block.RawBlock.serializer() },
    entry { Block.Table.serializer() },
)

object AttrSerializer : KSerializer<Attr> by TupleLikeSerializer(
    ::Attr,
    tupleProp(Attr::id),
    tupleProp(Attr::classes),
    tupleProp(Attr::properties)
)

object ListAttributesSerializer : KSerializer<ListAttributes> by TupleLikeSerializer(
    ::ListAttributes,
    tupleProp(ListAttributes::level),
    tupleProp(ListAttributes::numberStyle),
    tupleProp(ListAttributes::numberDelim)
)

object AlignmentSerializer : KSerializer<Alignment> by pandocEnumSerializer()
object ListNumberStyleSerializer : KSerializer<ListNumberStyle> by pandocEnumSerializer()
object ListNumberDelimSerializer : KSerializer<ListNumberDelim> by pandocEnumSerializer()
object QuoteTypeSerializer : KSerializer<QuoteType> by pandocEnumSerializer()
object MathTypeSerializer : KSerializer<MathType> by pandocEnumSerializer()
object CitationModeSerializer : KSerializer<CitationMode> by pandocEnumSerializer()
