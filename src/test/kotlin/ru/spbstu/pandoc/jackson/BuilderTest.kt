package ru.spbstu.pandoc.jackson

import org.testng.annotations.Test
import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.builder.blocks
import kotlin.test.assertEquals

class BuilderTest {

    @Test
    fun testBuilderSimple() {
        val manual =
                Block.Div(
                attr = Attr("", listOf("special")),
                contents = listOf(
                        Block.Plain(
                                inlines = listOf(
                                        Inline.Image(
                                                attr = Attr(id = "img"),
                                                altText = listOf(),
                                                target = Target("image.png")
                                        )
                                )
                        )
                )
        )
        val built = blocks {
            div {
                clazz = "special"

                plain {
                    image(Target("image.png")) {
                        id = "img"
                    }
                }
            }
        }.first()


        assertEquals(manual, built)

    }

    @Test
    fun testBuilderTable() {
        val build = blocks {
            table {
                caption {
                    emph { str { "Da table" } }
                }

                header(Alignment.AlignCenter) {
                    plain { str { "Column 1" } }
                }

                header(Alignment.AlignLeft) {
                    plain { str { "Column 2" } }
                }

                header(Alignment.AlignLeft) {
                    plain { str { "Column 3" } }
                }

                row {
                    cell {
                        plain { str { "Cell in column 1" } }
                    }
                    cell {
                        plain { str { "Cell in column 2" } }
                    }
                    cell {
                        plain { str { "Cell in column 3" } }
                    }
                }

            }
        }.first()

        val manual = Block.Table(
                caption = listOf(Inline.Emph(listOf(Inline.Str("Da table")))),
                alignments = listOf(Alignment.AlignCenter, Alignment.AlignLeft, Alignment.AlignLeft),
                widths = listOf(1.0, 1.0, 1.0),
                columnHeaders = listOf(
                        listOf(Block.Plain(listOf(Inline.Str("Column 1")))),
                                listOf(Block.Plain(listOf(Inline.Str("Column 2")))),
                                        listOf(Block.Plain(listOf(Inline.Str("Column 3"))))
                ),
                rows = listOf(
                        listOf(
                                listOf(Block.Plain(listOf(Inline.Str("Cell in column 1")))),
                                listOf(Block.Plain(listOf(Inline.Str("Cell in column 2")))),
                                listOf(Block.Plain(listOf(Inline.Str("Cell in column 3"))))
                        )
                )
        )

        assertEquals(manual, build)
    }
}