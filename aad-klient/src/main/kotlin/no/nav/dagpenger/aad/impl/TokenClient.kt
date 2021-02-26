package no.nav.dagpenger.aad.impl

import com.github.benmanes.caffeine.cache.Cache
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import no.nav.dagpenger.aad.api.TokenClientConfiguration
import no.nav.dagpenger.aad.http.AccessToken
import no.nav.dagpenger.aad.http.AccessTokenRequest

internal abstract class TokenClient<T : TokenClientConfiguration>(
    private val httpClient: HttpClient,
    private val config: T
) {
    private val cache: Cache<AccessTokenRequest, AccessToken> = config.cache.create()

    private suspend fun doRequest(tokenRequest: AccessTokenRequest): AccessToken {
        return httpClient.submitForm(
            url = tokenRequest.tokenEndpointUri,
            formParameters = Parameters.build {
                tokenRequest.formParameters.forEach {
                    append(it.key, it.value)
                }
            }
        )
    }

    protected suspend fun getOrFetch(formParameters: () -> Map<String, String> = { mapOf() }): AccessToken {
        val accessTokenRequest =
            AccessTokenRequest(config.tokenEndPoint, this.config.formParameters + formParameters())
        return cache.getIfPresent(accessTokenRequest) ?: doRequest(accessTokenRequest).also {
            cache.put(
                accessTokenRequest,
                it
            )
        }
    }
}
