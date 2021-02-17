val fuelVersion = "2.1.0"

dependencies {
    implementation(Fuel.fuel)
    implementation(Fuel.library("gson"))
    implementation(Prometheus.common)
    testImplementation(Wiremock.standalone)
}
