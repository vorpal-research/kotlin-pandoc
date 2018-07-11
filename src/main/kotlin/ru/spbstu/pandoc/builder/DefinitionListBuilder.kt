package ru.spbstu.pandoc.builder

import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.DefinitionItem

@BuilderMarker
class DefinitionListBuilder {
    private val items: MutableList<DefinitionItem> = mutableListOf()
    fun build(): List<DefinitionItem> = items

    fun item(desc: InlineBuilder.() -> Unit, body: BlockBuilder.() -> Unit) {
        val descBuilder = InlineBuilder()
        val bodyBuilder = BlockBuilder()

        descBuilder.desc()
        bodyBuilder.body()

        items += DefinitionItem(descBuilder.build(), listOf(bodyBuilder.build()))
    }
}
