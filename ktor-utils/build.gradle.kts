plugins {
    `common-kotlin`
}
dependencies {
    val ktorVersion = "3.5.2"
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("commons-codec:commons-codec:1.22.1")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
}
