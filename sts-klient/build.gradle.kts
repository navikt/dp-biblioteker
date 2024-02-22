plugins {
    `common-kotlin`
}
dependencies {
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.auth.jvm)
    implementation(libs.ktor.serialization.jackson)
    implementation("io.prometheus:simpleclient_common:0.16.0")

    testImplementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotest.assertions.core)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
}
