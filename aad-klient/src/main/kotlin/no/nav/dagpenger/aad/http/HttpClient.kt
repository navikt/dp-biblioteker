package no.nav.dagpenger.aad.http

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.http
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson

internal data class AccessTokenRequest(
    val tokenEndpointUri: String,
    val formParameters: Map<String, String>,
)

internal data class AccessToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_int")
    val expiresIn: Int,
)

fun defaultHttpClient(httpClientEngine: HttpClientEngine = CIO.create()) =
    HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
        }
        engine {
            System.getenv("HTTP_PROXY")?.let {
                this.proxy = ProxyBuilder.http(it)
            }
        }
    }
