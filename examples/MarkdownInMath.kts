#!/bin/env kscript

@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("ru.spbstu:kotlin-pandoc:0.0.4")
@file:DependsOn("com.atlassian.commonmark:commonmark:0.11.0")

import org.commonmark.node.Emphasis
import org.commonmark.node.Node
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Code
import org.commonmark.parser.Parser
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.text.TextContentRenderer
import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.PandocVisitor
import ru.spbstu.pandoc.makeFilter

private object Visitor : PandocVisitor() {
    fun transformMath(text: String): String {
        val parser = Parser.builder().build()!!
        val parsed = parser.parse(text)
        return TextContentRenderer.builder().nodeRendererFactory { context ->
            object : NodeRenderer {
                val context = context!!

                override fun getNodeTypes(): Set<Class<out Node>> = setOf(
                        Emphasis::class.java,
                        StrongEmphasis::class.java,
                        Code::class.java
                )

                override fun render(node: Node) = when(node) {
                    is Emphasis -> {
                        context.writer.write("\\mathit{")
                        context.render(node.firstChild)
                        context.writer.write("}")
                    }
                    is StrongEmphasis -> {
                        context.writer.write("\\mathbf{")
                        context.render(node.firstChild)
                        context.writer.write("}")
                    }
                    is Code -> {
                        context.writer.write("\\mathtt{")
                        context.writer.write(node.literal)
                        context.writer.write("}")
                    }
                    else -> {}
                }
            }
        }.build().render(parsed)
    }

    override fun visit(i: Inline.Math): Inline = i.copy(text = transformMath(i.text))
}

makeFilter(Visitor)
