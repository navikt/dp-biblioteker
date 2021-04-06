
dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32") {
            because("To align with ktor version")
        }
    }
    implementation(Ktor.library("client-cio-jvm"))
    implementation(Ktor.library("client-jackson"))
    implementation(Ktor.library("client-logging"))
    implementation(Ktor.library("client-auth-jvm"))
    implementation(Prometheus.common)

    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation(Ktor.library("client-mock"))
}
