package no.nav.dagpenger.texas

import io.ktor.client.HttpClient

class EntraKlient(
    tokenEndpoint: String,
    tokenExchangeEndpoint: String,
    introspectEndpoint: String,
    httpClient: HttpClient = defaultHttpClient(),
) {
    companion object {
        val IDENTITY_PROVIDER = IdentityProvider.ENTRA_ID
    }

    private val texasKlient: TexasKlient =
        TexasKlient(
            tokenEndpoint = tokenEndpoint,
            tokenExchangeEndpoint = tokenExchangeEndpoint,
            introspectEndpoint = introspectEndpoint,
            httpClient = httpClient,
        )

    suspend fun accessToken(
        target: String,
        resource: String? = null,
        skipCache: Boolean = true,
    ): TokenResponse =
        texasKlient.accessToken(
            target = target,
            identityProvider = IDENTITY_PROVIDER,
            resource = resource,
            skipCache = skipCache,
        )

    suspend fun exchangeToken(
        target: String,
        token: String,
        skipCache: Boolean = false,
    ): TokenResponse =
        texasKlient.exchangeToken(
            target = target,
            token = token,
            identityProvider = IDENTITY_PROVIDER,
            skipCache = skipCache,
        )
}
