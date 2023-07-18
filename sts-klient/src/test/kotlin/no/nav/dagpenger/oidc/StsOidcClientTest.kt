package no.nav.dagpenger.oidc

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import org.junit.jupiter.api.Test

class StsOidcClientTest {
    private val expires = 300L

    @Test
    fun `fetch open id token from sts server `() {
        val engine = MockEngine { request ->
            request.method shouldBe HttpMethod.Get
            request.url.toString() shouldBe "https://localhost/rest/v1/sts/token/?grant_type=client_credentials&scope=openid"
            request.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
            request.headers[HttpHeaders.Authorization] shouldMatch "Basic\\s[a-zA-Z0-9]*="
            this.respond(
                content = body(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val oidcToken: OidcToken = StsOidcClient("https://localhost/", "username", "password", engine).oidcToken()
        oidcToken shouldBe OidcToken("token", "openid", expires)
    }

    @Test
    fun `fetch open id token from sts server and token is cached `() {
        var requestCount = 0
        val engine = MockEngine {
            requestCount++
            this.respond(
                content = body(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val stsOidcClient = StsOidcClient("https://localhost/", "username", "password", engine)

        val firstCall: OidcToken = stsOidcClient.oidcToken()
        firstCall shouldBe OidcToken("token", "openid", expires)

        val secondCall: OidcToken = stsOidcClient.oidcToken()
        secondCall shouldBe OidcToken("token", "openid", expires)

        requestCount shouldBe 1
    }

    @Test
    fun `Fetch new token if token has expired`() {
        var requestCount = 0
        val engine = MockEngine {
            requestCount++
            this.respond(
                content = body(-10),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val stsOidcClient = StsOidcClient("https://localhost/", "username", "password", engine)

        (1..3).forEach { _ -> stsOidcClient.oidcToken() }
        requestCount shouldBe 3
    }

    @Test
    fun `fetch open id token from sts on server error`() {
        val engine = MockEngine { respondBadRequest() }
        runCatching {
            StsOidcClient(
                "https://localhost/",
                "username",
                "password",
                engine,
            ).oidcToken()
        }.also { result ->
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeInstanceOf<StsOidcClientException>()
        }
    }

    private fun body(expireTime: Long = expires) =
        """
                {
                    "access_token": "token",
                    "token_type" : "openid",
                    "expires_in" : $expireTime
                }

        """.trimIndent()
}
