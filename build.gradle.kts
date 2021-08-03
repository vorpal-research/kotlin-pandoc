import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

version = "0.0.15-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.vorpal-research.science")
}

val jacksonVersion = "2.10.2"
val kxSerializationVersion = "1.2.2"
val ktuplesVersion = "0.0.2.4"
val kotlinVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kxSerializationVersion")

    implementation("ru.spbstu:kotlinx-warnings-jvm:$kotlinVersion")
    implementation("ru.spbstu:ktuples:$ktuplesVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kxSerializationVersion")
}
