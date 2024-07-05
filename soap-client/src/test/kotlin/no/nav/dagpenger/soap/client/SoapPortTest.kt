package no.nav.dagpenger.soap.client

import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.mockk
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import org.apache.cxf.service.factory.ServiceConstructionException
import org.apache.cxf.ws.security.trust.STSClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SoapPortTest {
    @Test
    fun `kan opprette klient`() {
        val stsClient = mockk<STSClient>()

        createSoapClient<YtelseskontraktV3> {
            sts = stsClient
            stsAllowInsecure = true

            endpoint = "foo"
            wsdl = "wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl"
        }.also {
            it.shouldBeInstanceOf<YtelseskontraktV3>()
        }
    }

    @Test
    fun `kan opprette flere klienter`() {
        val stsClient = mockk<STSClient>()

        val client1 =
            createSoapClient<YtelseskontraktV3> {
                sts = stsClient
                stsAllowInsecure = true

                endpoint = "foo"
                wsdl = "wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl"
            }.also {
                it.shouldBeInstanceOf<YtelseskontraktV3>()
            }

        val client2 =
            createSoapClient<YtelseskontraktV3> {
                sts = stsClient
                stsAllowInsecure = true

                endpoint = "foo"
                wsdl = "wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl"
            }.also {
                it.shouldBeInstanceOf<YtelseskontraktV3>()
            }

        client1 shouldNotBeSameInstanceAs client2
    }

    @Test
    fun `kan overstyre svcName og PortName for å lage klient`() {
        val stsClient = mockk<STSClient>()

        val e: ServiceConstructionException =
            assertThrows<ServiceConstructionException> {
                createSoapClient<YtelseskontraktV3> {
                    sts = stsClient
                    stsAllowInsecure = true
                    endpoint = "foo"
                    wsdl = "wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl"
                    svcName = "Blupr"
                    portName = "Blarp"
                }
            }
        assertEquals(
            "Could not find definition for service {http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding}Blupr.",
            e.message,
        )
    }
}
