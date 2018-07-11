#!/bin/env kscript

@file:MavenRepository("jitpack.io","https://jitpack.io" )
@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("com.github.stathissideris:ditaa:v0.11.0")
@file:DependsOn("ru.spbstu:kotlin-pandoc:0.0.1")

import ru.spbstu.pandoc.*

class IdCollector : PandocVisitor() {
    val blocks: MutableMap<String, Block> = mutableMapOf()
    val inlines: MutableMap<String, Inline> = mutableMapOf()

}

makeFilter(object : PandocVisitor() {

})
