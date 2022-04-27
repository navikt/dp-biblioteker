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
    implementation(Ktor2.Client.library("logging"))
    implementation(Ktor2.Client.library("content-negotiation"))
    implementation(Ktor2.Client.library("cio"))
    implementation("io.ktor:ktor-serialization-jackson:${Ktor2.version}")
    implementation(Jackson.jsr310)
    testImplementation(Junit5.api)
    testImplementation(Mockk.mockk)
    testRuntimeOnly(Junit5.engine)
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.10")
}

tasks.withType<org.gradle.jvm.tasks.Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
