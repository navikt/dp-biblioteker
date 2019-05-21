val ktorVersion = "1.2.0"

dependencies {
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("commons-codec:commons-codec:1.12")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}
