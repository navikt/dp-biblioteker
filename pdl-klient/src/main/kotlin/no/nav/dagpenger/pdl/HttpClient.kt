package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.aad.api.ClientCredentialsClient

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

@JvmOverloads
fun createAccessTokenFun(scope: String, config: Map<String, String> = mapOf()): () -> String {
    val client =
        ClientCredentialsClient(ConfigurationMap(config) overriding ConfigurationProperties.systemProperties() overriding EnvironmentVariables) {
            this.scope { add(scope) }
        }
    return { runBlocking { client.getAccessToken() } }
}

fun createRequestBuilder(tokenFun: () -> String): HttpRequestBuilder.() -> Unit {
    return {
        header("TEMA", "DAG")
        header(HttpHeaders.Authorization, """Bearer ${tokenFun()}""")
    }
}

class GraphqlClientError(message: String) : RuntimeException(message)
