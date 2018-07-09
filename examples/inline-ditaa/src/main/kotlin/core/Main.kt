package core

import org.stathissideris.ascii2image.core.RenderingOptions
import org.stathissideris.ascii2image.graphics.BitmapRenderer
import org.stathissideris.ascii2image.graphics.Diagram
import org.stathissideris.ascii2image.text.TextGrid
import ru.spbstu.ktuples.Tuple
import ru.spbstu.ktuples.Tuple3
import ru.spbstu.pandoc.Block
import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.PandocVisitor
import ru.spbstu.pandoc.makeFilter
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun main(args: Array<String>)  = makeFilter(
        object : PandocVisitor() {
            override fun visit(b: Block.CodeBlock): Block {
                if(b.attr.v0 != "ditaa") return super.visit(b)

                val grid = TextGrid()
                grid.initialiseWithLines(b.text.lines().mapTo(ArrayList()){ StringBuilder(it) }, null)
                val diag = Diagram(grid, null)

                val uuid = UUID.fromString(b.text)
                val tmpName = "$uuid.png"
                ImageIO.write(BitmapRenderer().renderToImage(
                        diag, RenderingOptions()), "png",
                        File(tmpName))

                return Block.Div(
                    attr = Tuple("", listOf(), listOf()),
                    contents = listOf(
                        Block.Plain(
                            inlines = listOf(
                                    Inline.Image(
                                            attr = Tuple3("", listOf(), listOf()),
                                            altText = listOf(),
                                            target = Tuple(tmpName, tmpName)
                                    )
                            )
                        )
                    )
                )

            }
        }
)
