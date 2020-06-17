dependencies {
    implementation(Ktor.server)
    implementation(Ktor.auth)
    implementation(Ktor.library("client"))
    implementation("commons-codec:commons-codec:1.12")
    implementation(Prometheus.common)
    testImplementation(Ktor.ktorTest)
    testImplementation(Wiremock.standalone)
    testImplementation(KoTest.assertions)
}
