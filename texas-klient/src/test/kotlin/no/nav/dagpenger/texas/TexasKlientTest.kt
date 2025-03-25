@file:Suppress("ktlint:standard:max-line-length")

package no.nav.dagpenger.texas

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TexasKlientTest {
    @Test
    fun `riktig http verb,headers, body  og response ved kall til Texas`() {
        var tokenRequest: HttpRequestData? = null
        var tokenExchangeRequest: HttpRequestData? = null
        var introspectRequest: HttpRequestData? = null
        runBlocking {
            val mockEngine =
                MockEngine { request ->
                    when (request.url.segments.last()) {
                        "token" -> {
                            tokenRequest = request
                            respond(
                                // language=json
                                content = """{ "access_token": "token", "expires_in": 9999, "token_type": "Bearer" }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }

                        "tokenexchange" -> {
                            tokenExchangeRequest = request
                            respond(
                                // language=json
                                content = """{ "access_token": "obo_token", "expires_in": 9999, "token_type": "Bearer" }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }

                        "introspect" -> {
                            introspectRequest = request
                            respond(
                                // language=json
                                content = """{ "active": true, "sub": "1234567890", "name": "John Doe", "int": 12223, "admin": true }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }

                        else -> error("bad url")
                    }
                }
            TexasKlient(
                tokenEndpoint = "http://localhost/token",
                tokenExchangeEndpoint = "http://localhost/tokenexchange",
                introspectEndpoint = "http://localhost/introspect",
                httpClient = defaultHttpClient(mockEngine),
            ).let { client ->
                client.accessToken(
                    target = "target",
                    identityProvider = IdentityProvider.ENTRA_ID,
                    skipCache = true,
                ) shouldBe
                    TokenResponse(
                        access_token = "token",
                        token_type = "Bearer",
                        expires_in = 9999,
                    )
                requireNotNull(tokenRequest).let { it ->
                    //language=json
                    String(it.body.toByteArray()) shouldEqualJson """{"identity_provider":"azuread","target":"target", "skip_cache":true}"""
                    it.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
                    it.method shouldBe HttpMethod.Post
                }

                client.exchangeToken(
                    target = "target",
                    token = "user_token",
                    identityProvider = IdentityProvider.ENTRA_ID,
                    skipCache = true,
                ) shouldBe
                    TokenResponse(
                        access_token = "obo_token",
                        token_type = "Bearer",
                        expires_in = 9999,
                    )
                requireNotNull(tokenExchangeRequest).let {
                    //language=json
                    String(it.body.toByteArray()) shouldEqualJson """{"identity_provider":"azuread","target":"target","user_token":"user_token", "skip_cache":true}"""
                    it.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
                    it.method shouldBe HttpMethod.Post
                }

                client.introspect(
                    identityProvider = IdentityProvider.ENTRA_ID,
                    token = "introspect_token",
                ) shouldBe
                    IntrospectResponse.Valid(
                        claims =
                            mapOf(
                                "sub" to "1234567890",
                                "name" to "John Doe",
                                "int" to 12223,
                                "admin" to true,
                            ),
                    )
                requireNotNull(introspectRequest).let {
                    //language=json
                    String(it.body.toByteArray()) shouldEqualJson """{"identity_provider":"azuread","token":"introspect_token"}"""
                    it.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
                    it.method shouldBe HttpMethod.Post
                }
            }
        }
    }

    @Test
    fun `test av feil håndtering`() {
        runBlocking {
            val mockEngine =
                MockEngine { request ->
                    when (request.url.segments.last()) {
                        "token" -> {
                            respond(
                                content = """{"error": "badrequest", "error_description": "badrequest description"}""",
                                status = HttpStatusCode.BadRequest,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }

                        "tokenexchange" -> {
                            respond(
                                content = """{"error": "internal server error", "error_description": "internal server error description"}""",
                                status = HttpStatusCode.InternalServerError,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }

                        else -> error("bad url")
                    }
                }

            TexasKlient(
                tokenEndpoint = "http://localhost/token",
                tokenExchangeEndpoint = "http://localhost/tokenexchange",
                introspectEndpoint = "http://localhost/introspect",
                httpClient = defaultHttpClient(mockEngine),
            ).let { client ->
                shouldThrow<BadRequestException> {
                    client.accessToken(
                        target = "target",
                        identityProvider = IdentityProvider.ENTRA_ID,
                        skipCache = true,
                    )
                }.errorResponse shouldBe ErrorResponse("badrequest", "badrequest description")

                shouldThrow<ServerError> {
                    client.exchangeToken(
                        target = "target",
                        token = "user_token",
                        identityProvider = IdentityProvider.ENTRA_ID,
                        skipCache = true,
                    )
                }.errorResponse shouldBe
                    ErrorResponse("internal server error", "internal server error description")
            }
        }
    }
}
