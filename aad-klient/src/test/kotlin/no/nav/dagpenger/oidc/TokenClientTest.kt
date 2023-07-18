package no.nav.dagpenger.oidc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.natpryce.konfig.ConfigurationMap
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.aad.api.ClientCredentialsClient
import no.nav.dagpenger.aad.http.AccessToken
import no.nav.dagpenger.aad.http.defaultHttpClient
import org.junit.jupiter.api.Test

internal class TokenClientTest {
    val env = ConfigurationMap(
        "AZURE_APP_CLIENT_ID" to "clientId",
        "AZURE_APP_CLIENT_SECRET" to "clientSecret",
        "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT" to "https://token.endpoint/",

    )

    val tokenJsonString: String = jacksonObjectMapper().writeValueAsString(AccessToken("token", 100))

    @Test
    fun `ClientCredentials client should call correct service with formparameters from configuration`() {
        lateinit var requestData: HttpRequestData

        val token = runBlocking {
            ClientCredentialsClient(
                env,
                defaultHttpClient(
                    MockEngine.create {
                        addHandler { request ->
                            requestData = request
                            respond(
                                content = tokenJsonString,
                                headers = headersOf("Content-Type", "application/json"),
                            )
                        }
                    },
                ),
            ) {
                scope {
                    add("scope1")
                    add("scope2")
                }
            }.getAccessToken()
        }
        token shouldBe "token"

        requestData.method shouldBe HttpMethod.Post
        requestData.url.toString() shouldBe "https://token.endpoint/"

        (requestData.body as FormDataContent).formData.let {
            it.contains("client_id", "clientId") shouldBe true
            it.contains("client_secret", "clientSecret") shouldBe true
            it.contains("grant_type", "client_credentials") shouldBe true
            it.contains("scope", "scope1 scope2") shouldBe true
        }
    }
}
