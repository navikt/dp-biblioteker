package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Config

internal fun <T> GraphQLClientResponse<T>.responseParser(): T {
    if (!this.errors.isNullOrEmpty()) {
        this.errors!!.asSequence().map { it.message }.joinToString(";").let {
            throw GraphqlClientError(it)
        }
    }
    return when (this.data) {
        null -> throw GraphqlClientError("No data")
        else -> this.data!!
    }
}

val proxyAwareHttpClient = HttpClient(engineFactory = CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    engine {
        System.getenv("HTTP_PROXY")?.let {
            this.proxy = ProxyBuilder.http(Url(it))
        }
    }
}

val defaultHttpClient = HttpClient(engineFactory = CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}

@JvmOverloads
fun createAccessTokenFun(
    scope: String,
    config: Map<String, String> = mapOf(),
    httpClient: HttpClient = defaultHttpClient,
): () -> String {
    val azureAd = OAuth2Config.AzureAd(config)
    val client = CachedOauth2Client(
        tokenEndpointUrl = azureAd.tokenEndpointUrl,
        authType = azureAd.clientSecret(),
        httpClient = httpClient
    )
    return { runBlocking { client.clientCredentials(scope).accessToken } }
}

fun createRequestBuilder(tokenFun: () -> String): HttpRequestBuilder.() -> Unit {
    return {
        header("TEMA", "DAG")
        header(HttpHeaders.Authorization, """Bearer ${tokenFun()}""")
    }
}

class GraphqlClientError(message: String) : RuntimeException(message)
