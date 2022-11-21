plugins {
    id("io.github.ermadmi78.kobby") version "2.1.0"
}

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:${Jackson.version}")
}

java {
    val mainJavaSourceSet: SourceDirectorySet = sourceSets.getByName("main").java
    val graphqlDir = "$buildDir/generated/sources/kobby/main/kotlin"
    mainJavaSourceSet.srcDirs(graphqlDir)
}

tasks.withType<org.gradle.jvm.tasks.Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

// Kobby Plugin Configuration
kobby {
    // Schema location and parsing rules configuration
    schema {
        // GraphQL schema files to generate Kobby DSL
        // By default all `**/*.graphqls` files in `src/main/resources`
        files = null // org.gradle.api.file.FileCollection

        // Configuration of schema files location scanning
        scan {
            // Root directory to scan schema files
            dir = "src/main/resources"

            // ANT style include patterns to scan schema files
            includes = listOf("**/*.graphql") // Iterable<String>

            // ANT style exclude patterns to scan schema files
            excludes = null // Iterable<String>
        }
    }

    // Configuration of Kotlin DSL generation
    kotlin {
        // Is Kotlin DSL generation enabled
        enabled = true

        // Mapping GraphQL scalars to Kotlin classes
        scalars = mapOf(
            "Date" to typeOf("java.time", "LocalDate"),
            "DateTime" to typeOf("java.time", "LocalDateTime"),
            "ID" to typeOf("kotlin", "String"),
        )

        // Is root package name for generated DSL
        // should be relative to GraphQL schema directory
        relativePackage = true

        // Root package name for generated DSL
        packageName = "no.nav.dagpenger.pdl"

        // Output directory for generated DSL
        // org.gradle.api.file.Directory
        outputDirectory = project.layout.buildDirectory
            .dir("generated/sources/kobby/main/kotlin").get()

        // Configuration of DSL context generation (entry point to DSL)
        context {
            // Context package name relative to root package name
            // By default is empty
            packageName = null // String

            // Name of generated DSL context
            // By default is name of GraphQL schema file
            // or `graphql` if there are multiple schema files
            name = "pdl"

            // Prefix of generated `Context` interface
            // By default is capitalized context name
            prefix = null // String

            // Postfix of generated `Context` interface
            postfix = null // String

            // Name of `query` function in `Context` interface
            query = "query"

            // Name of `mutation` function in `Context` interface
            mutation = "mutation"

            // Name of `subscription` function in `Context` interface
            subscription = "subscription"
        }

        // Configuration of DTO classes generation
        dto {
            // Package name for DTO classes. Relative to root package name.
            packageName = "dto"

            // Prefix of DTO classes
            // generated from GraphQL objects, interfaces and unions
            prefix = null // String

            // Postfix of DTO classes
            // generated from GraphQL objects, interfaces and unions
            postfix = "Dto"

            // Prefix of DTO classes generated from GraphQL enums
            enumPrefix = null // String

            // Postfix of DTO classes generated from GraphQL enums
            enumPostfix = null // String

            // Prefix of DTO classes generated from GraphQL inputs
            inputPrefix = null // String

            // Postfix of DTO classes generated from GraphQL inputs
            inputPostfix = null // String

            // Kobby can generate `equals` and `hashCode` functions for entities classes
            // based on fields marked with `@primaryKey` directive.
            // This parameter provides an ability to apply the same generation logic to DTO classes
            applyPrimaryKeys = false

            // Configuration of Jackson annotations generation for DTO classes
            jackson {
                // Is Jackson annotations generation enabled
                // By default `true` if `com.fasterxml.jackson.core:jackson-annotations`
                // artifact is in the project dependencies
                enabled = true // Boolean

                // Customize the @JsonTypeInfo annotation's `use` property.
                typeInfoUse = "NAME"

                // Customize the @JsonTypeInfo annotation's `include` property.
                typeInfoInclude = "PROPERTY"

                // Customize the @JsonTypeInfo annotation's `property` property.
                typeInfoProperty = "__typename"

                // Customize the @JsonInclude annotation's `value` property.
                jsonInclude = "NON_ABSENT"
            }

            // Configuration of DTO builders generation
            builder {
                // Is DTO builders generation enabled
                enabled = true

                // Prefix of DTO builder classes
                prefix = null // String

                // Postfix of DTO builder classes
                postfix = "Builder"

                // Name of builder based `copy` function for DTO classes
                copyFun = "copy"
            }

            // Configuration of helper DTO classes generation
            // for implementing the GraphQL interaction protocol
            graphQL {
                // Is helper DTO classes generation enabled
                enabled = true

                // Package name for helper DTO classes relative to DTO package name
                packageName = "graphql"

                // Prefix for helper DTO classes
                prefix = null // String

                // Postfix for helper DTO classes
                postfix = null // String
            }
        }

        // Configuration of DSL Entities interfaces generation
        entity {
            // Is entities interfaces generation enabled
            enabled = true

            // Package name for entities interfaces relative to root package name
            packageName = "entity"

            // Prefix for entities interfaces
            prefix = null // String

            // Postfix for entities interfaces
            postfix = null // String

            // Inherit context interface in entity interface
            // https://github.com/ermadmi78/kobby/issues/20
            contextInheritanceEnabled = false

            // Generate context access function in entity interface
            // https://github.com/ermadmi78/kobby/issues/20
            contextFunEnabled = false

            // Context access function name in entity interface
            // https://github.com/ermadmi78/kobby/issues/20
            contextFunName = "__context"

            // Name of `withCurrentProjection` function in entity interface
            withCurrentProjectionFun = "__withCurrentProjection"

            // Configuration of DSL Entity Projection interfaces generation
            projection {
                // Prefix for projection interfaces
                projectionPrefix = null // String

                // Postfix for projection interfaces
                projectionPostfix = "Projection"

                // Name of projection argument in field functions
                projectionArgument = "__projection"

                // Prefix for projection fields
                // that are not marked with the directive `@default`
                withPrefix = null // String

                // Postfix for projection fields
                // that are not marked with the directive `@default`
                withPostfix = null // String

                // Prefix for default projection fields
                // (marked with the directive `@default`)
                withoutPrefix = "__without"

                // Postfix for default projection fields
                // (marked with the directive `@default`)
                withoutPostfix = null // String

                // Name of `minimize` function in projection interface
                minimizeFun = "__minimize"

                // Prefix for qualification interfaces
                qualificationPrefix = null // String

                // Postfix for qualification interfaces
                qualificationPostfix = "Qualification"

                // Prefix for qualified projection interface
                qualifiedProjectionPrefix = null // String

                // Postfix for qualified projection interface
                qualifiedProjectionPostfix = "QualifiedProjection"

                // Prefix for qualification functions
                onPrefix = "__on"

                // Postfix for qualification functions
                onPostfix = null // String
            }

            // Configuration of DSL Entity Selection interfaces generation
            selection {
                // Prefix for selection interfaces
                selectionPrefix = null // String

                // Postfix for selection interfaces
                selectionPostfix = "Selection"

                // Name of selection argument in field functions
                selectionArgument = "__selection"

                // Prefix for query interfaces
                queryPrefix = null // String

                // Postfix for query interfaces
                queryPostfix = "Query"

                // Name of query argument in field functions
                queryArgument = "__query"
            }
        }

        // Configuration of DSL Entities implementation classes generation
        impl {
            // Package name for entities implementation classes
            // relative to root package name
            packageName = "entity.impl"

            // Prefix for entities implementation classes
            prefix = null // String

            // Postfix for entities implementation classes
            postfix = "Impl"

            // Is implementation classes should be internal
            internal = true

            // Prefix for inner fields in implementation classes
            innerPrefix = "__inner"

            // Postfix for inner fields in implementation classes
            innerPostfix = null // String
        }

        // Configuration of adapter classes generation
        adapter {
            // Configuration of Ktor adapter classes generation
            ktor {
                // Is simple Ktor adapter generation enabled
                // By default `true` if `io.ktor:ktor-client-cio`
                // artifact is in the project dependencies
                simpleEnabled = false // Boolean

                // Is composite Ktor adapter generation enabled
                // By default `true` if `io.ktor:ktor-client-cio`
                // artifact is in the project dependencies
                compositeEnabled = false // Boolean

                // Package name for Ktor adapter classes relative to root package name
                packageName = "adapter.ktor"

                // Prefix for Ktor adapter classes
                prefix = null // String

                // Postfix for Ktor adapter classes
                postfix = "KtorAdapter"
            }
        }

        // Configuration of resolver interfaces generation
        resolver {
            // Is resolver interfaces generation enabled
            // By default `true` if `com.graphql-java-kickstart:graphql-java-tools`
            // artifact is in the project dependencies
            enabled = null // Boolean

            // Is wrap subscription resolver functions result in `org.reactivestreams.Publisher`
            // By default `true` if `org.reactivestreams:reactive-streams`
            // artifact is in the project dependencies
            publisherEnabled = null // Boolean

            // Package name for resolver interfaces relative to root package name
            packageName = "resolver"

            // Prefix for resolver interfaces
            // By default is capitalized context name
            prefix = null // String

            // Postfix for resolver interfaces
            postfix = "Resolver"

            // Name for parent object argument
            // By default is de-capitalized name of parent object type
            argument = null // String

            // If not null, Kobby will generate default implementation for
            // functions in resolver interfaces that looks like:
            // TODO("$toDoMessage")
            toDoMessage = null // String
        }
    }
}
