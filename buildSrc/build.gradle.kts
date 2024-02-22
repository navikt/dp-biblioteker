plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "6.25.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.22.0")
}

spotless {
    kotlinGradle {
        ktlint()
    }
}
