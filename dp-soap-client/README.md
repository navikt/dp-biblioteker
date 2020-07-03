# dp-soap-client

Legg til dette i build.gradle.kts

```kotlin
val tjenestespesifikasjonerVersion = "1.2019.09.25-00.21-49b69f0625e0"
fun tjenestespesifikasjon(name: String) = "no.nav.tjenestespesifikasjoner:$name:$tjenestespesifikasjonerVersion"

dependencies {
    implementation(Dagpenger.Biblioteker.Soap.client)
    implementation(tjenestespesifikasjon("ytelseskontrakt-v3-tjenestespesifikasjon"))
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()

    // Make sure the cxf service files are handled correctly so that the SOAP services work.
    // Ref https://stackoverflow.com/questions/45005287/serviceconstructionexception-when-creating-a-cxf-web-service-client-scalajava
    transform(ServiceFileTransformer::class.java) {
        setPath("META-INF/cxf")
        include("bus-extensions.txt")
    }
}
```

Lag klient med:
```kotlin
    val soapStsClient = stsClient(
        stsUrl = config.soapSTSClient.endpoint,
        credentials = config.soapSTSClient.username to config.soapSTSClient.password
    )

    createSoapClient<YtelseskontraktV3> {
        sts = stsClient
        stsAllowInsecure = true

        endpoint = "foo"
        wsdl = "wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl"
    }
```