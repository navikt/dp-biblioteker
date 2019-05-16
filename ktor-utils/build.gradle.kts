val ktorVersion = "1.2.0"

dependencies {
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}
