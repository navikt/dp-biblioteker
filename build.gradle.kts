
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.3.21"
    id("org.jetbrains.dokka") version "0.9.17" apply false
    id("com.diffplug.gradle.spotless") version "3.13.0"
    `java-library`
    `maven-publish`
}

allprojects {
    repositories {
        jcenter()
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
    apply(plugin = "com.diffplug.gradle.spotless")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")

    val junitJupiterVersion = "5.3.1"

    dependencies {
        implementation(kotlin("stdlib"))
        testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        testCompile("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
        testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitJupiterVersion")
        testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")
        testImplementation(kotlin("test"))
        testImplementation(kotlin("test-junit"))
    }


    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
        }
    }

    tasks.withType<Wrapper> {
        gradleVersion = "5.4.1"
    }


    val dokka = tasks.withType<DokkaTask> {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(dokka)
        archiveClassifier.set("javadoc")
        from(buildDir.resolve("javadoc"))
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }


    spotless {
        kotlin {
            ktlint("0.31.0")
        }
        kotlinGradle {
            target("*.gradle.kts", "additionalScripts/*.gradle.kts")
            ktlint("0.31.0")
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
