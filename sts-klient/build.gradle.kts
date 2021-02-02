
dependencies {
    implementation(Ktor.library("client-cio-jvm"))
    implementation(Ktor.library("client-jackson"))
    implementation(Ktor.library("client-logging"))
    implementation(Ktor.library("client-auth-jvm"))
    implementation(Prometheus.common)
    testImplementation(Wiremock.standalone)
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation( Ktor.library("client-mock"))
}
