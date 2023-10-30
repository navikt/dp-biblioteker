plugins {
    `common-kotlin`
}
dependencies {
    implementation(Ktor.server)
    implementation(Ktor.auth)
    implementation("commons-codec:commons-codec:1.12")
    testImplementation(Ktor.ktorTest)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
}
