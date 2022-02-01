import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}") {
            because("To avoid version conflict as reported by gradle")
        }
    }
}

plugins {
    id("com.expediagroup.graphql") version Graphql.version
}
dependencies {
    implementation(Graphql.library("client-jackson"))
    implementation(project(":oauth2-klient"))
    implementation(Ktor.library("client-logging"))
    implementation(Ktor.library("client-jackson"))
    implementation(Graphql.library("ktor-client")) {
        exclude("com.expediagroup", "graphql-kotlin-client-serialization")
    }
    testImplementation(Junit5.api)
    testImplementation(Mockk.mockk)
    testRuntimeOnly(Junit5.engine)
}

tasks.withType<org.gradle.jvm.tasks.Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

val schema = "schema.graphql"
graphql {
    client {
        packageName = "no.nav.pdl"
        schemaFile = file("$projectDir/src/main/resources/pdl/pdl-api-schema.graphql")
        queryFileDirectory = "$projectDir/src/main/resources/pdl"
        serializer = GraphQLSerializer.JACKSON
        customScalars = listOf(
            GraphQLScalar("Date", "java.time.LocalDate", "no.nav.dagpenger.pdl.graphql.converter.DateScalar"),
            GraphQLScalar(
                "DateTime",
                "java.time.LocalDateTime",
                "no.nav.dagpenger.pdl.graphql.converter.DateTimeScalar"
            )
        )
    }
}

tasks.named("compileKotlin") {
    dependsOn("graphqlGenerateClient")
}

// To get intellij to make sense of generated sources from graphql client
java {
    val mainJavaSourceSet: SourceDirectorySet = sourceSets.getByName("main").java
    val graphqlDir = "$buildDir/generated/source/graphql/main"
    mainJavaSourceSet.srcDirs(graphqlDir)
}
