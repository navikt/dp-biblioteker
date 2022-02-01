dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}") {
            because("To avoid version conflict as reported by gradle")
        }
    }

    api(Konfig.konfig)
    api("no.nav.security:token-client-core:1.3.10")
    implementation(Ktor.library("client-logging"))
    implementation(Ktor.library("client-cio-jvm"))
    implementation(Ktor.library("client-jackson"))

    testImplementation(Ktor.library("client-mock"))
    testImplementation(KoTest.assertions)
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
}
