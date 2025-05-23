plugins {
    `kotlin-dsl`
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jlleitschuh.gradle:ktlint-gradle:12.1.2")
}
