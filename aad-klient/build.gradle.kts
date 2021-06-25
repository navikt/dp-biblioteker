dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}") {
            because("To avoid version conflict as reported by gradle")
        }
    }

    api(Konfig.konfig)
    implementation(Ktor.library("client-cio-jvm"))
    implementation(Ktor.library("client-jackson"))
    implementation(Ktor.library("client-logging"))
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.1")

    testImplementation(Ktor.library("client-mock"))
    testImplementation(KoTest.assertions)
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
}
