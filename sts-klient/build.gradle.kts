val fuelVersion = "2.1.0"

dependencies {
    implementation(Fuel.fuel)
    implementation(Ktor.library("client-cio-jvm"))
    implementation(Ktor.library("client-jackson"))
    implementation(Ktor.library("client-auth-jvm"))
    implementation(Fuel.library("gson"))
    implementation(Prometheus.common)
    testImplementation(Wiremock.standalone)
}
