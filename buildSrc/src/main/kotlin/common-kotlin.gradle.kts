import com.diffplug.spotless.LineEnding
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("java-library")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(Junit5.api)
    testImplementation(Junit5.params)
    testImplementation(KoTest.assertions)
    testImplementation(KoTest.runner)
    testRuntimeOnly(Junit5.engine)
}

java {
    toolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
    }
}


group = "com.github.navikt"
version = "1.0-SNAPSHOT"

val artifactDescription = "Bibliotek som holder nåværende og historiske grunnbeløp for Dagpenger domenet"
val repoUrl = "https://github.com/navikt/dp-grunnbelop.git"
val scmUrl = "scm:git:https://github.com/navikt/dp-grunnbelop.git"

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts {
    add("archives", sourcesJar)
}


configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint()
        targetExclude("**/generated/**") // ignore generated stuff
    }

    kotlinGradle {
        ktlint()
    }
    // Workaround for <https://github.com/diffplug/spotless/issues/1644>
    // using idea found at
    // <https://github.com/diffplug/spotless/issues/1527#issuecomment-1409142798>.
    lineEndings = LineEnding.PLATFORM_NATIVE // or any other except GIT_ATTRIBUTES
}

