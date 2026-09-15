plugins {
    `common-kotlin`
}

dependencies {
    val ktorVersion = "3.5.2"
    implementation("io.ktor:ktor-client-auth-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
}
