plugins {
    `common-kotlin`
}
dependencies {
    api(Ktor2.Client.library("core-jvm"))
    implementation(Prometheus.common)
    testImplementation(Ktor2.Client.library("mock-jvm"))
    testImplementation(KoTest.assertions)
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
}
