pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "kotlin-pandoc"
