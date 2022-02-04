dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}") {
            because("To avoid version conflict as reported by gradle")
        }
    }
}

dependencies {
    implementation(project(":oauth2-klient"))
    implementation(project(":pdl-klient-kobby"))
    implementation(Ktor.library("client-logging"))
    implementation(Ktor.library("client-jackson"))
    implementation(Ktor.library("client-cio"))
    implementation(Jackson.jsr310)
    testImplementation(Junit5.api)
    testImplementation(Mockk.mockk)
    testRuntimeOnly(Junit5.engine)
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.10")
}

tasks.withType<org.gradle.jvm.tasks.Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
