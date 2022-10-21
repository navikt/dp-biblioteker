dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}") {
            because("To avoid version conflict as reported by gradle")
        }
    }

    api(Konfig.konfig)
    api("no.nav.security:token-client-core:2.1.6")
    implementation(Ktor2.Client.library("logging"))
    implementation(Ktor2.Client.library("cio-jvm"))
    implementation(Ktor2.Client.library("content-negotiation"))
    implementation("io.ktor:ktor-serialization-jackson:${Ktor2.version}")

    testImplementation(Ktor2.Client.library("mock"))
    testImplementation(KoTest.assertions)
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
    // FOr E2E
    testImplementation("io.kubernetes:client-java:16.0.0")
}
