package no.nav.dagpenger.ktor.client.auth.providers

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.features.auth.Auth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class BearerAuthProviderTest {
    @Test
    fun testOidcAuthString() {
        assertEquals(
            "Bearer oidc-token",
            buildAuthString("oidc-token")
        )
    }

    @Test
    fun testWithoutAuthorizationNegotiation() = testWithClient(
        sendWithoutRequest = true,
        requestHandler = { request ->
            assertEquals(
                "Bearer static-token",
                request.headers[HttpHeaders.Authorization]
            )
            respondOk()
        }
    )

    @Test
    fun testWithAuthorizationNegotiation() = testWithClient(
        requestHandler = { request ->
            when (val auth = request.headers[HttpHeaders.Authorization]) {
                is String -> {
                    assertEquals("Bearer static-token", auth)
                    respondOk()
                }
                else -> respondError(
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(
                        HttpHeaders.WWWAuthenticate to listOf("Bearer")
                    )
                )
            }
        }
    )

    @Test
    fun testWithAuthorizationNegotiationAndRealm() = testWithClient(
        realm = "secrets",
        requestHandler = { request ->
            when (val auth = request.headers[HttpHeaders.Authorization]) {
                is String -> {
                    assertEquals("Bearer static-token", auth)
                    respondOk()
                }
                else -> respondError(
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(
                        HttpHeaders.WWWAuthenticate to listOf("Bearer realm=secrets")
                    )
                )
            }
        }
    )

    private fun testWithClient(
        sendWithoutRequest: Boolean = false,
        realm: String? = null,
        requestHandler: MockRequestHandler,
        test: HttpResponse.() -> Unit = { assertEquals(HttpStatusCode.OK, this.status) }
    ) {
        HttpClient(MockEngine) {
            install(Auth) {
                bearer {
                    tokenProvider = { "static-token" }
                    this.realm = realm
                    this.sendWithoutRequest = sendWithoutRequest
                }
            }

            engine {
                addHandler(requestHandler)
            }
        }.run {
            runBlocking {
                get<HttpResponse>("/").also {
                    test(it)
                }
            }
        }
    }

    private fun buildAuthString(token: String): String =
        BearerAuthProvider({ token }).constructTokenAuthValue()
}
