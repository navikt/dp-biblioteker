val fuelVersion = "2.1.0"

dependencies {
    implementation(Fuel.fuel)
    implementation(Fuel.library("jackson"))
    implementation(Prometheus.common)
    testImplementation(Wiremock.standalone)
}
