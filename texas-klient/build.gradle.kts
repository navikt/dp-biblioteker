plugins {
    `common-kotlin`
}

dependencies {

    api(libs.konfig)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson)

    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")

    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest.assertions)
    testImplementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
}
