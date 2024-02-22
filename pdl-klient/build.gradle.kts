plugins {
    `common-kotlin`
}

dependencies {
    implementation(project(":oauth2-klient"))
    implementation(project(":pdl-klient-kobby"))
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.jackson.datatype.jsr310)
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testImplementation(libs.mockk)
    testImplementation("io.kubernetes:client-java:18.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.4.14")
    testImplementation(libs.kotest.assertions.core)
}

tasks.withType<org.gradle.jvm.tasks.Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
