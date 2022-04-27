dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}") {
            because("To avoid version conflict as reported by gradle")
        }
    }

    api(Konfig.konfig)
    implementation(Ktor2.Client.library("cio"))
    implementation(Ktor2.Client.library("content-negotiation"))
    implementation(Ktor2.Client.library("logging"))
    implementation("io.ktor:ktor-serialization-jackson:${Ktor2.version}")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.2")

    testImplementation(Ktor2.Client.library("mock"))
    testImplementation(KoTest.assertions)
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
}
