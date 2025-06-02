import io.github.ermadmi78.kobby.kobby

plugins {
    `common-kotlin`
    id("io.github.ermadmi78.kobby") version "5.2.0"
}

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:${libs.versions.jackson.get()}")
}

java {
    val mainJavaSourceSet: SourceDirectorySet = sourceSets.getByName("main").java
    val graphqlDir = "$buildDir/generated/sources/kobby/main/kotlin"
    mainJavaSourceSet.srcDirs(graphqlDir)
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("generated") }
    }
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn("kobbyKotlin")
}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask> {
    dependsOn("kobbyKotlin")
}

kobby {
    kotlin {
        // Root package name for generated DSL
        packageName = "no.nav.dagpenger.pdl"

        // Mapping GraphQL scalars to Kotlin classes
        scalars =
            mapOf(
                "Date" to typeOf("java.time", "LocalDate"),
                "DateTime" to typeOf("java.time", "LocalDateTime"),
                "ID" to typeOf("kotlin", "String"),
            )

        // Configuration of DSL context generation (entry point to DSL)
        context {
            // Name of generated DSL context
            // By default is name of GraphQL schema file
            // or `graphql` if there are multiple schema files
            name = "pdl"
        }

        entity {
            // Generate context access function in entity interface
            // https://github.com/ermadmi78/kobby/issues/20
            contextFunEnabled = false

            // Context access function name in entity interface
            // https://github.com/ermadmi78/kobby/issues/20
            contextFunName = "__context"
        }
    }
}
