package ru.spbstu.pandoc.jackson

import org.testng.annotations.Test
import ru.spbstu.ktuples.Tuple
import ru.spbstu.pandoc.Attr
import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.Target
import ru.spbstu.pandoc.builder.blocks
import java.io.File
import kotlin.test.assertEquals

class BuilderTest {

    @Test
    fun testBuilderSimple() {
        val manual =
                Block.Div(
                attr = Attr("", listOf("special"), listOf()),
                contents = listOf(
                        Block.Plain(
                                inlines = listOf(
                                        Inline.Image(
                                                attr = Attr(),
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
                    image(Target("image.png"))
                }
            }
        }.first()


        assertEquals(manual, built)

    }

}