import com.diffplug.spotless.LineEnding
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.URI

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


group = "no.nav.dagpenger"

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("name") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/navikt/dp-biblioteker")
            credentials {
                val githubUser: String? by project
                val githubPassword: String? by project
                username = githubUser
                password = githubPassword
            }
        }
    }
}




configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint()
        targetExclude("**/generated/**") // ignore generated stuff
    }

    kotlinGradle {
        ktlint()
    }
}

