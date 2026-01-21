plugins {
    `common-kotlin`
}

val tjenestespesifikasjonerVersion = "2648.7dd4e44"

fun tjenestespesifikasjon(name: String) = "com.github.navikt.tjenestespesifikasjoner:$name:$tjenestespesifikasjonerVersion"

val cxfVersion = "4.1.4"

dependencies {
    implementation("de.huxhorn.sulky:de.huxhorn.sulky.ulid:8.3.0")

    implementation("jakarta.xml.ws:jakarta.xml.ws-api:4.0.2")
    implementation("com.sun.xml.ws:jaxws-tools:4.0.2")

    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    api("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("jakarta.activation:jakarta.activation-api:2.1.4")

    testImplementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    testImplementation(tjenestespesifikasjon("ytelseskontrakt-v3-tjenestespesifikasjon"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions.core)
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
