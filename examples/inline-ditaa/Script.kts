#!/bin/env kscript

@file:MavenRepository("jitpack.io","https://jitpack.io" )
@file:DependsOn("com.github.stathissideris:ditaa:v0.11.0")
@file:DependsOn("com.github.belyaev-mikhail:kotlin-pandoc:master-SNAPSHOT")

import org.stathissideris.ascii2image.core.ConversionOptions
import org.stathissideris.ascii2image.core.RenderingOptions
import org.stathissideris.ascii2image.graphics.BitmapRenderer
import org.stathissideris.ascii2image.graphics.Diagram
import org.stathissideris.ascii2image.text.TextGrid
import ru.spbstu.ktuples.Tuple
import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.builder.blocks
import java.io.File
import java.util.*
import javax.imageio.ImageIO

makeFilter(
        object : PandocVisitor() {
            override fun visit(b: Block.CodeBlock): Block {
                if("ditaa" !in b.attr.v1) return super.visit(b)

                val grid = TextGrid()
                grid.initialiseWithLines(b.text.lines().mapTo(ArrayList()){ StringBuilder(it) }, null)
                val diag = Diagram(grid, ConversionOptions())

                val tmpFile = createTempFile(suffix = ".png")
                val tmpName = tmpFile.absolutePath
                ImageIO.write(BitmapRenderer().renderToImage(
                        diag, RenderingOptions()), "png",
                        tmpFile)

                return blocks {
                    div {
                        plain {
                            image(Target(tmpName))
                        }
                    }
                }.first()
            }
        }
)
