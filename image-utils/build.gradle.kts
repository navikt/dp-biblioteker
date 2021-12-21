dependencies {
    implementation("org.apache.pdfbox:preflight:2.0.25")
    implementation("org.apache.pdfbox:pdfbox:2.0.25")
    implementation("org.apache.tika:tika-core:2.2.0")
    implementation("javax.activation:activation:1.1")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    testImplementation(Junit5.api)
    testRuntimeOnly(Junit5.engine)
}
