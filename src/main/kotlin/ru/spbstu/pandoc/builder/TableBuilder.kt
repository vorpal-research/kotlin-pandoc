package ru.spbstu.pandoc.builder

import ru.spbstu.pandoc.Alignment
import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.TableCell

@BuilderMarker
class TableBuilder {
    private var caption: List<Inline> = listOf()
    private val alignments: MutableList<Alignment> = mutableListOf()
    private val widths: MutableList<Double> = mutableListOf()
    private val columnHeaders: MutableList<TableCell> = mutableListOf()
    private val rows: MutableList<List<TableCell>> = mutableListOf()

    fun caption(body: InlineBuilder.() -> Unit) {
        caption = inlines(body)
    }

    fun header(alignment: Alignment = Alignment.AlignDefault,
               width: Double = 1.0,
               body: BlockBuilder.() -> Unit) {
        alignments += alignment
        widths += width
        columnHeaders += blocks(body)
    }

    fun row(body: RowBuilder.() -> Unit) {
        val builder = RowBuilder(mutableListOf())
        builder.body()
        if(builder.build().isNotEmpty()) rows += builder.build()
    }

    @BuilderMarker
    inner class RowBuilder(private val row: MutableList<TableCell>) {
        fun cell(body: BlockBuilder.() -> Unit) {
            row += blocks(body)
        }
        fun build(): List<TableCell> = row
    }

    fun build(): Block.Table {
        require(columnHeaders.size == widths.size)
        require(columnHeaders.size == alignments.size)
        require(rows.all { it.size == columnHeaders.size })
        return Block.Table(
                caption = caption,
                alignments = alignments,
                widths = widths,
                columnHeaders = columnHeaders,
                rows = rows
        )
    }


}
