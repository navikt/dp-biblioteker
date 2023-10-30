plugins {
    `common-kotlin`
}

dependencies {
    val ktorVersion = "1.6.8"
    implementation("io.ktor:ktor-client-auth-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
}
