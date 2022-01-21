dependencies {
    implementation("org.apache.pdfbox:preflight:2.0.25")
    implementation("org.apache.pdfbox:pdfbox:2.0.25")
    implementation("org.apache.tika:tika-core:2.2.0")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    runtimeOnly("javax.activation:activation:1.1")
    runtimeOnly("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    testImplementation(Junit5.api)
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testRuntimeOnly(Junit5.engine)
}