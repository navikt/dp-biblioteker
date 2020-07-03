val tjenestespesifikasjonerVersion = "1.2019.09.25-00.21-49b69f0625e0"
fun tjenestespesifikasjon(name: String) = "no.nav.tjenestespesifikasjoner:$name:$tjenestespesifikasjonerVersion"
val cxfVersion = "3.3.4"

dependencies {
    implementation(Ulid.ulid)

    implementation("javax.xml.ws:jaxws-api:2.3.1")
    implementation("com.sun.xml.ws:jaxws-tools:2.3.0.2")

    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    api("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("javax.activation:activation:1.1.1")
    implementation("no.nav.helse:cxf-prometheus-metrics:dd7d125")

    testImplementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    testImplementation(tjenestespesifikasjon("ytelseskontrakt-v3-tjenestespesifikasjon"))
    testImplementation(Mockk.mockk)
}
