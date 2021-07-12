package no.nav.dagpenger.aad.api

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.EnvironmentVariables
import io.ktor.client.HttpClient
import no.nav.dagpenger.aad.http.defaultHttpClient
import no.nav.dagpenger.aad.impl.TokenClient

interface ClientCredentialsClient {
    suspend fun getAccessToken(): String
}

interface OnBehalfClient {
    suspend fun getAccessToken(onBehalfOfToken: String): String
}

interface TokenXClient {
    suspend fun getAccessToken(): String
}

@JvmOverloads
fun ClientCredentialsClient(
    env: Configuration = EnvironmentVariables,
    httpClient: HttpClient = defaultHttpClient(),
    configure: TokenClientConfiguration.ClientCredential.() -> Unit
): ClientCredentialsClient = object :
    TokenClient<TokenClientConfiguration.ClientCredential>(
        httpClient,
        TokenClientConfiguration.ClientCredential(env).apply(configure)
    ),
    ClientCredentialsClient {
    override suspend fun getAccessToken(): String = getOrFetch().accessToken
}

@JvmOverloads
fun OnBehalfClient(
    env: Configuration = EnvironmentVariables,
    httpClient: HttpClient = defaultHttpClient(),
    configure: TokenClientConfiguration.Onbehalf.() -> Unit
): OnBehalfClient = object :
    TokenClient<TokenClientConfiguration.Onbehalf>(
        httpClient,
        TokenClientConfiguration.Onbehalf(env).apply(configure)
    ),
    OnBehalfClient {
    override suspend fun getAccessToken(onBehalfOfToken: String): String = getOrFetch() {
        mapOf("assertion" to onBehalfOfToken)
    }.accessToken
}

@JvmOverloads
fun TokenXClient(
    env: Configuration = EnvironmentVariables,
    httpClient: HttpClient = defaultHttpClient(),
    configure: TokenClientConfiguration.TokenX.() -> Unit
): TokenXClient = object :
    TokenClient<TokenClientConfiguration.TokenX>(
        httpClient,
        TokenClientConfiguration.TokenX(env).apply(configure)
    ),
    TokenXClient {
    override suspend fun getAccessToken(): String = getOrFetch().accessToken
}
