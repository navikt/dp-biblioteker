plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "7.0.2"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.22.0")
}

spotless {
    kotlinGradle {
        ktlint()
    }
}
