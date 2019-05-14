
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.3.21"
    id("org.jetbrains.dokka") version "0.9.17" apply false
}


allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {
    val junitJupiterVersion = "5.3.1"
    group = "no.nav.dagpenger"
    version = "1.0-SNAPSHOT"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.dokka")

    dependencies {
        implementation(kotlin("stdlib"))
        testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        testCompile("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
        testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
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
            events("passed", "skipped", "failed")
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
        getArchiveClassifier().set("sources")
        from(sourceSets["main"].allSource)
    }

    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(dokka)
        classifier = "javadoc"
        from(buildDir.resolve("javadoc"))
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }




}
