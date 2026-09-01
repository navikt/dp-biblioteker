plugins {
    `common-kotlin`
}
dependencies {
    api(libs.ktor.client.cio)
    implementation("io.prometheus:prometheus-metrics-core:1.8.0")
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotest.assertions.core)
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
}
