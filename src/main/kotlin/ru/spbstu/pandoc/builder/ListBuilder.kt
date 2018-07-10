package ru.spbstu.pandoc.builder

import ru.spbstu.pandoc.Block

class ListBuilder {
    private val items: MutableList<List<Block>> = mutableListOf()
    fun build(): List<List<Block>> = items

    fun item(body: BlockBuilder.() -> Unit) {
        val builder = BlockBuilder()
        builder.body()
        items += builder.build()
    }
}
