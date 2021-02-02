package no.nav.dagpenger.oidc

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.RegexPattern
import io.kotest.data.headers
import io.kotest.matchers.shouldHave
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StsOidcClientTest {

    companion object {
        val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

        @BeforeAll
        @JvmStatic
        fun start() {
            server.start()
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            server.stop()
        }
    }

    @BeforeEach
    fun configure() {
        configureFor(server.port())
    }

    private val expires = 300L

    @Test
    fun `fetch open id token from sts server`() {

        stubFor(
            WireMock.get(urlEqualTo("/rest/v1/sts/token/?grant_type=client_credentials&scope=openid"))
                .withHeader("Authorization", RegexPattern("Basic\\s[a-zA-Z0-9]*="))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body())
                )
        )

        runBlocking {
            val stsOidcClient = StsOidcClient(server.url(""), "username", "password")
            val oidcToken: OidcToken = stsOidcClient.oidcToken()
            assertEquals(oidcToken, OidcToken("token", "openid", expires))
        }
    }

    @Test
    fun `fetch open id token from sts server with KtorMock`() {

        runBlocking {
            val engine = MockEngine{
                respondOk(body())
            }
            val stsOidcClient = StsOidcClient(server.url(""), "username", "password", engine)
            val oidcToken: OidcToken = stsOidcClient.oidcToken()
            assertEquals(oidcToken, OidcToken("token", "openid", expires))
        }
    }

    @Test
    fun `fetch open id token from sts server and token is cached `() {

        stubFor(
            WireMock.get(urlEqualTo("/cached/rest/v1/sts/token/?grant_type=client_credentials&scope=openid"))
                .withHeader("Authorization", RegexPattern("Basic\\s[a-zA-Z0-9]*="))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body())
                )
        )
        val stsOidcClient = StsOidcClient(server.url("cached"), "username", "password")

        runBlocking {
            val firstCall: OidcToken = stsOidcClient.oidcToken()

            assertEquals(firstCall, OidcToken("token", "openid", expires))

            val secondCall: OidcToken = stsOidcClient.oidcToken()

            assertEquals(secondCall, OidcToken("token", "openid", expires))

            verify(
                exactly(1),
                getRequestedFor(urlEqualTo("/cached/rest/v1/sts/token/?grant_type=client_credentials&scope=openid"))
                    .withHeader("Authorization", RegexPattern("Basic\\s[a-zA-Z0-9]*="))
            )
        }

    }

    @Test
    fun `fetch open id token from sts on server error`() {

        stubFor(
            WireMock.get(urlEqualTo("/rest/v1/sts/token/?grant_type=client_credentials&scope=openid"))
                .withHeader("Authorization", RegexPattern("Basic\\s[a-zA-Z0-9]*="))
                .willReturn(
                    WireMock.serverError()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("FAILED")
                )
        )

        val stsOidcClient = StsOidcClient(server.url(""), "username", "password")
        runBlocking {
            val result = runCatching { stsOidcClient.oidcToken() }
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is StsOidcClientException)
        }
    }

    fun body() =
        """
                {
                    "access_token": "token",
                    "token_type" : "openid",
                    "expires_in" : $expires
                }

        """.trimIndent()
}
