val fuelVersion = "2.1.0"

dependencies {
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")
    implementation("io.prometheus:simpleclient:0.6.0")
    testImplementation("com.github.tomakehurst:wiremock-standalone:2.19.0")
}
