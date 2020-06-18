dependencies {
    implementation(Ktor.library("client"))
    implementation(Prometheus.common)
    testImplementation(Ktor.library("client-mock-jvm"))
    testImplementation(KoTest.assertions)
}
