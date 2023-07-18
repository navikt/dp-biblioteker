plugins {
    `common-kotlin`
}
dependencies {
    api(Ktor2.Client.library("core-jvm"))
    implementation(Prometheus.common)
    testImplementation(Ktor2.Client.library("mock-jvm"))
    testImplementation(KoTest.assertions)
}
