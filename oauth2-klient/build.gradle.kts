plugins {
    `common-kotlin`
}

dependencies {

    api(libs.konfig)
    api("no.nav.security:token-client-core:5.0.28")
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotest.assertions.core)
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")

    // FOr E2E
    testImplementation("io.kubernetes:client-java:16.0.0")
}
