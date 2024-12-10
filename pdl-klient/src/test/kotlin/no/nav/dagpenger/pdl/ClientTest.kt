@file:Suppress("NonAsciiCharacters")

package no.nav.dagpenger.pdl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Config
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Manuel test")
class ClientTest {
    private val azureAd =
        OAuth2Config.AzureAd(
            mapOf(
                OAuth2Config.AzureAd.CLIENT_ID_KEY to "",
                OAuth2Config.AzureAd.CLIENT_SECRET_KEY to "",
                OAuth2Config.AzureAd.TOKEN_ENDPOINT_KEY to "",
            ),
        )

    private val tokenX =
        OAuth2Config.TokenX(
            mapOf(
                OAuth2Config.TokenX.CLIENT_ID_KEY to "",
                OAuth2Config.TokenX.PRIVATE_JWK_KEY to """ """,
                OAuth2Config.TokenX.WELLKNOWN_URL_KEY to "",
            ),
        )

    private val azureAdClient =
        CachedOauth2Client(
            tokenEndpointUrl = azureAd.tokenEndpointUrl,
            authType = azureAd.clientSecret(),
        )

    private val tokenXClient =
        CachedOauth2Client(
            tokenEndpointUrl = tokenX.tokenEndpointUrl,
            authType = tokenX.privateKey(),
        )

    val httpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    registerModules(JavaTimeModule())
                    setSerializationInclusion(JsonInclude.Include.NON_NULL)
                }
            }
            defaultRequest {
                header("TEMA", "DAG")
            }

            install(Logging) {
                level = LogLevel.ALL
            }
        }

    @Test
    fun `Bruk token x for å hente ut person`() {
        val accessToken =
            tokenXClient.tokenExchange(
                token = """""",
                audience = "",
            ).access_token

        val client =
            createPersonOppslag(
                url = "https://pdl-api.dev.intern.nav.no/graphql",
                httpClient = httpClient,
            )
        runBlocking {
            client.hentPerson(
                fnr = "01038401226",
                headersMap = mapOf(HttpHeaders.Authorization to "Bearer $accessToken"),
            )
        }
    }

    @Test
    fun `Bruk token x for å hente ut identifikatorer`() {
        val accessToken =
            tokenXClient.tokenExchange(
                token = """""",
                audience = "",
            ).access_token

        val client =
            createPersonOppslag(
                url = "https://pdl-api.dev.intern.nav.no/graphql",
                httpClient = httpClient,
            )
        runBlocking {
            client.hentIdenter(
                ident = "01038401226",
                grupper = emptyList(),
                historikk = false,
                headersMap = mapOf(HttpHeaders.Authorization to "Bearer $accessToken"),
            )
        }
    }

    @Test
    fun `Bruk azure ad client credentials for å hente ut personer`() {
        val client =
            createPersonOppslagBolk(
                url = "https://pdl-api.dev.intern.nav.no/graphql",
                httpClient = httpClient,
            )
        runBlocking {
            client.hentPersoner(
                // fake
                listOf("01038401226", "20028418370", "25108621845"),
                mapOf(
                    HttpHeaders.Authorization to
                        "Bearer ${azureAdClient.clientCredentials("api://dev-fss.pdl.pdl-api/.default").access_token}",
                ),
            ).onEach {
                println(it)
            }
        }
    }

    @Test
    fun `Bruk azure ad client credentials for å hente ut person`() {
        val personOppslag =
            createPersonOppslag(
                url = "https://pdl-api.dev.intern.nav.no/graphql",
                httpClient = httpClient,
            )
        runBlocking {
            personOppslag.hentPerson(
                "14108009241",
                mapOf(
                    HttpHeaders.Authorization to
                        "Bearer ${azureAdClient.clientCredentials("api://dev-fss.pdl.pdl-api/.default").access_token}",
                ),
            ).also { println(it) }
        }
    }

    @Test
    fun `Bruk azure ad client credentials for å hente ut identifikatorer`() {
        val personOppslag =
            createPersonOppslag(
                url = "https://pdl-api.dev.intern.nav.no/graphql",
                httpClient = httpClient,
            )
        runBlocking {
            personOppslag.hentIdenter(
                "14108009241",
                emptyList(),
                true,
                mapOf(
                    HttpHeaders.Authorization to
                        "Bearer ${azureAdClient.clientCredentials("api://dev-fss.pdl.pdl-api/.default").access_token}",
                ),
            ).also { println(it) }
        }
    }
}
