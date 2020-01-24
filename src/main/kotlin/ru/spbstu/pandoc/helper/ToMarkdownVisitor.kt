package ru.spbstu.pandoc.helper

import ru.spbstu.pandoc.*

class ToMarkdownVisitor : PandocVisitor() {

    val sb: StringBuilder = StringBuilder()

    fun visit(attr: Attr) {
        sb.append("{")
        sb.append(" #${attr.id}")
        for(klass in attr.classes) {
            sb.append(".${klass} ")
        }
        for((k, v) in attr.properties) {
            sb.append(" $k=$v")
        }
        sb.append(" }")
    }

    override fun visit(v: MetaValue): MetaValue {
        return super.visit(v)
    }

    override fun visit(v: MetaValue.MetaMap): MetaValue {
        return super.visit(v)
    }

    override fun visit(v: MetaValue.MetaList): MetaValue {
        return super.visit(v)
    }

    override fun visit(v: MetaValue.MetaBool): MetaValue {
        return super.visit(v)
    }

    override fun visit(v: MetaValue.MetaString): MetaValue {
        return super.visit(v)
    }

    override fun visit(v: MetaValue.MetaInlines): MetaValue {
        return super.visit(v)
    }

    override fun visit(v: MetaValue.MetaBlocks): MetaValue {
        return super.visit(v)
    }

    override fun visit(b: Block.Para): Block {
        sb.appendln()
        super.visit(b)
        return b
    }

    override fun visit(b: Block.LineBlock): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.CodeBlock): Block {
        sb.append("``` ")
        visit(b.attr)
        sb.appendln()
        sb.append(b.text)
        sb.appendln("```")
        return b
    }

    override fun visit(b: Block.RawBlock): Block {
        sb.append(b.text)
        return super.visit(b)
    }

    override fun visit(b: Block.BlockQuote): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.OrderedList): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.BulletList): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.DefinitionList): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.Header): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.HorizontalRule): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.Table): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.Div): Block {
        return super.visit(b)
    }

    override fun visit(b: Block.Null): Block {
        return super.visit(b)
    }

    override fun visit(bs: List<Block>, token: Block?): List<Block> {
        return super.visit(bs, token)
    }

    override fun visit(di: DefinitionItem): DefinitionItem {
        return super.visit(di)
    }

    override fun visit(i: Inline): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Str): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Emph): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Strong): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Strikeout): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Superscript): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Subscript): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.SmallCaps): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Quoted): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Cite): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Code): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Space): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.SoftBreak): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.LineBreak): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Math): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.RawInline): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Link): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Image): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Note): Inline {
        return super.visit(i)
    }

    override fun visit(i: Inline.Span): Inline {
        return super.visit(i)
    }

    override fun visit(iis: List<Inline>, token: Inline?): List<Inline> {
        return super.visit(iis, token)
    }

    override fun visit(c: Citation): Citation {
        return super.visit(c)
    }

    override fun visit(doc: Pandoc): Pandoc {
        return super.visit(doc)
    }
}
