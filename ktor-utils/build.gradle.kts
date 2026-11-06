plugins {
    `common-kotlin`
}
dependencies {
    val ktorVersion = "3.6.1"
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("commons-codec:commons-codec:1.23.0")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
}
