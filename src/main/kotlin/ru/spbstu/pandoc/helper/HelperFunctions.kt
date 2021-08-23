package ru.spbstu.pandoc.helper

import kotlinx.warnings.Warnings
import ru.spbstu.pandoc.*

internal class ToStringVisitor : PandocVisitor() {
    val builder = StringBuilder()

    override fun visit(b: Block.CodeBlock): Block {
        builder.append(b.text)
        return super.visit(b)
    }

    override fun visit(b: Block.RawBlock): Block {
        builder.append(b.text)
        return super.visit(b)
    }

    override fun visit(i: Inline.Str): Inline {
        builder.append(i.text)
        return super.visit(i)
    }

    override fun visit(i: Inline.Quoted): Inline {
        val quotes = when(i.type) {
            QuoteType.DoubleQuote -> "\""
            QuoteType.SingleQuote -> "\'"
        }
        builder.append(quotes)
        val res = super.visit(i)
        builder.append(quotes)
        return res
    }

    override fun visit(i: Inline.Code): Inline {
        builder.append(i.text)
        return super.visit(i)
    }

    override fun visit(i: Inline.Space): Inline {
        builder.append(" ")
        return super.visit(i)
    }

    override fun visit(i: Inline.SoftBreak): Inline {
        builder.append("\n")
        return super.visit(i)
    }

    override fun visit(i: Inline.LineBreak): Inline {
        builder.append("\n")
        return super.visit(i)
    }

    override fun visit(i: Inline.Math): Inline {
        builder.append(i.text)
        return super.visit(i)
    }

    override fun visit(i: Inline.RawInline): Inline {
        builder.append(i.text)
        return super.visit(i)
    }
}

fun Inline.getContentsAsText(): String {
    val visitor = ToStringVisitor()
    accept(visitor)
    return visitor.builder.toString()
}

fun Block.getContentsAsText(): String {
    val visitor = ToStringVisitor()
    accept(visitor)
    return visitor.builder.toString()
}

@JvmName("getInlineListContentsAsText")
fun List<Inline>.getContentsAsText(): String {
    val visitor = ToStringVisitor()
    accept(visitor)
    return visitor.builder.toString()
}

@JvmName("getBlockListContentsAsText")
fun List<Block>.getContentsAsText(): String {
    val visitor = ToStringVisitor()
    accept(visitor)
    return visitor.builder.toString()
}

@Suppress(Warnings.UNCHECKED_CAST)
fun <T: Attributes> T.copy(attr: Attr = this.attr): T = when(val sub: Attributes = this) {
    is Block.CodeBlock -> sub.copy(attr) as T
    is Block.Div -> sub.copy(attr = attr) as T
    is Block.Header -> sub.copy(attr = attr) as T
    is Inline.Code -> sub.copy(attr = attr) as T
    is Inline.Image -> sub.copy(attr = attr) as T
    is Inline.Link -> sub.copy(attr = attr) as T
    is Inline.Span -> sub.copy(attr = attr) as T
}


