import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Kotlin.version
    id(Spotless.spotless) version Spotless.version
    `java-library`
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {

    group = "com.github.navikt"
    version = "1.0-SNAPSHOT"

    val artifactDescription = "Libraries for Dagpenger"
    val repoUrl = "https://github.com/navikt/dp-biblioteker.git"
    val scmUrl = "scm:git:https://github.com/navikt/dp-biblioteker.git"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = Spotless.spotless)
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation(Junit5.api)
        testImplementation(Junit5.params)
        testImplementation(KoTest.assertions)
        testImplementation(KoTest.runner)
        testRuntimeOnly(Junit5.engine)
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "1.8"
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

    tasks.withType<Wrapper> {
        gradleVersion = "6.0.1"
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    artifacts {
        add("archives", sourcesJar)
    }

    spotless {
        kotlin {
            ktlint(Ktlint.version)
        }
        kotlinGradle {
            target("*.gradle.kts", "buildSrc/**/*.kt*")
            ktlint(Ktlint.version)
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifact(sourcesJar.get())

                pom {
                    description.set(artifactDescription)
                    name.set(project.name)
                    url.set(repoUrl)
                    withXml {
                        asNode().appendNode("packaging", "jar")
                    }
                    licenses {
                        license {
                            name.set("MIT License")
                            name.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            organization.set("NAV (Arbeids- og velferdsdirektoratet) - The Norwegian Labour and Welfare Administration")
                            organizationUrl.set("https://www.nav.no")
                        }
                    }

                    scm {
                        connection.set(scmUrl)
                        developerConnection.set(scmUrl)
                        url.set(repoUrl)
                    }
                }
            }
        }
    }
}
