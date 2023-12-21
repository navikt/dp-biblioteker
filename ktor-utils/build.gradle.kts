plugins {
    `common-kotlin`
}
dependencies {
    val ktorVersion = "1.6.8"
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("commons-codec:commons-codec:1.16.0")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
}
