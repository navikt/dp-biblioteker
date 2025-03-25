package no.nav.dagpenger.texas

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

data class TokenRequest(
    val identity_provider: String,
    val target: String,
    val resource: String? = null,
    val skip_cache: Boolean = false,
)

data class TokenExchangeRequest(
    val identity_provider: String,
    val target: String,
    val user_token: String,
    val skip_cache: Boolean = false,
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
)

data class ErrorResponse(
    val error: String,
    val error_description: String,
)

sealed class RequestError(
    val httpStatusCode: HttpStatusCode,
    val errorResponse: ErrorResponse,
) : RuntimeException()

class BadRequestException(httpStatusCode: HttpStatusCode, errorResponse: ErrorResponse) :
    RequestError(httpStatusCode, errorResponse)

class ServerError(httpStatusCode: HttpStatusCode, errorResponse: ErrorResponse) :
    RequestError(httpStatusCode, errorResponse)

class EntraKlient(
    tokenEndpoint: String,
    tokenExchangeEndpoint: String,
    httpClient: HttpClient = defaultHttpClient(),
) {
    companion object {
        const val IDENTITY_PROVIDER = "azuread"
    }

    private val texasKlient: TexasKlient =
        TexasKlient(
            tokenEndpoint = tokenEndpoint,
            tokenExchangeEndpoint = tokenExchangeEndpoint,
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

class TexasKlient(
    private val tokenEndpoint: String,
    private val tokenExchangeEndpoint: String,
    private val httpClient: HttpClient = defaultHttpClient(),
) {
    suspend fun accessToken(
        target: String,
        identityProvider: String,
        resource: String? = null,
        skipCache: Boolean,
    ): TokenResponse {
        return kotlin.runCatching {
            httpClient.post(tokenEndpoint) {
                header("Content-Type", "application/json")
                setBody(TokenRequest(identityProvider, target, resource, skipCache))
            }.body<TokenResponse>()
        }.onFailure {
        }.getOrThrow()
    }

    suspend fun exchangeToken(
        target: String,
        token: String,
        identityProvider: String,
        skipCache: Boolean,
    ): TokenResponse {
        return httpClient.post(tokenExchangeEndpoint) {
            header("Content-Type", "application/json")
            setBody(
                TokenExchangeRequest(
                    identity_provider = identityProvider,
                    target = target,
                    user_token = token,
                    skip_cache = skipCache,
                ),
            )
        }.body<TokenResponse>()
    }
}
