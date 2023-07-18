plugins {
    `common-kotlin`
}
dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32") {
            because("To align with ktor version")
        }
    }
    implementation(Ktor2.Client.library("cio"))
    implementation(Ktor2.Client.library("content-negotiation"))
    implementation(Ktor2.Client.library("logging"))
    implementation(Ktor2.Client.library("auth"))
    implementation("io.ktor:ktor-serialization-jackson:${Ktor2.version}")
    implementation(Prometheus.common)

    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation(Ktor2.Client.library("mock"))
}
