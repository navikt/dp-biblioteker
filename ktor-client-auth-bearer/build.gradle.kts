plugins {
    `common-kotlin`
}

dependencies {
    implementation(Ktor.library("client-auth-jvm"))
    testImplementation(Ktor.library("client"))
    testImplementation(Ktor.library("client-mock-jvm"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
}
