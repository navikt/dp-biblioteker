plugins {
    `common-kotlin`
}

dependencies {
    implementation("org.apache.pdfbox:preflight:2.0.26")
    implementation("org.apache.pdfbox:pdfbox:2.0.26")
    implementation("org.apache.tika:tika-core:3.2.3")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    runtimeOnly("javax.activation:activation:1.1.1")
    runtimeOnly("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${libs.versions.junit.get()}")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${libs.versions.junit.get()}")
    testImplementation(libs.kotest.assertions.core)
}
