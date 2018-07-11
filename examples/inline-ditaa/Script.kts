#!/bin/env kscript

@file:MavenRepository("jitpack.io","https://jitpack.io" )
@file:DependsOn("com.github.stathissideris:ditaa:v0.11.0")
@file:DependsOn("com.github.belyaev-mikhail:kotlin-pandoc:25107d0")

import org.stathissideris.ascii2image.core.ConversionOptions
import org.stathissideris.ascii2image.core.RenderingOptions
import org.stathissideris.ascii2image.graphics.BitmapRenderer
import org.stathissideris.ascii2image.graphics.Diagram
import org.stathissideris.ascii2image.graphics.SVGRenderer
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
                if("ditaa" !in b.attr.classes) return super.visit(b)

                val props = b.attr.propertiesMap()
                val altText = props["alt-text"]
                val format = props["format"]

                val grid = TextGrid()
                grid.initialiseWithLines(b.text.lines().mapTo(ArrayList()){ StringBuilder(it) }, null)
                val diag = Diagram(grid, ConversionOptions())

                val tmpName = if(format == "svg") {
                    val tmpFile = createTempFile(suffix = ".svg")
                    tmpFile.writeText(SVGRenderer().renderToImage(diag, RenderingOptions()))
                    tmpFile.absolutePath
                } else {
                    val tmpFile = createTempFile(suffix = ".png")
                    ImageIO.write(BitmapRenderer().renderToImage(
                            diag, RenderingOptions()), "png",
                            tmpFile)
                    tmpFile.absolutePath
                }

                return blocks {
                    div {
                        plain {
                            image(Target(tmpName, altText ?: tmpName))
                        }
                    }
                }.first()
            }
        }
)
