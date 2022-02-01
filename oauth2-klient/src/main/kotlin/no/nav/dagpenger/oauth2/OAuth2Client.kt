package no.nav.dagpenger.oauth2

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse

class OAuth2Client(
    private val tokenEndpointUrl: String,
    private val authType: AuthType,
    private val httpClient: HttpClient = defaultHttpClient(),
) {

    suspend fun onBehalfOf(token: String, scope: String) =
        accessToken(GrantRequest.OnBeHalfOf(token, scope, authType))

    suspend fun tokenExchange(token: String, audience: String) =
        accessToken(GrantRequest.TokenX(token, audience, authType))

    suspend fun clientCredentials(scope: String) =
        accessToken(GrantRequest.ClientCredentials(scope, authType))

    suspend fun accessToken(grantRequest: GrantRequest): OAuth2AccessTokenResponse {
        return httpClient.submitForm(
            url = tokenEndpointUrl,
            formParameters = Parameters.build {
                grantRequest.formParams.forEach {
                    this.append(it.key, it.value)
                }
            },
        )
    }
}

class CachedOauth2Client(
    tokenEndpointUrl: String,
    private val authType: AuthType,
    httpClient: HttpClient = defaultHttpClient(),
    loadingCacheBuilder: LoadingCacheBuilder = LoadingCacheBuilder(),
) {
    private val client = OAuth2Client(tokenEndpointUrl, authType, httpClient)
    private val cache = loadingCacheBuilder.cache {
        client.accessToken(it)
    }

    fun onBehalfOf(token: String, scope: String) =
        accessToken(GrantRequest.OnBeHalfOf(token, scope, authType))

    fun tokenExchange(token: String, audience: String) =
        accessToken(GrantRequest.TokenX(token, audience, authType))

    fun clientCredentials(scope: String) =
        accessToken(GrantRequest.ClientCredentials(scope, authType))

    private fun accessToken(grantRequest: GrantRequest): OAuth2AccessTokenResponse {
        return cache.get(grantRequest)
    }
}
