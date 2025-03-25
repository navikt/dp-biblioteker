package no.nav.dagpenger.texas

import com.fasterxml.jackson.annotation.JsonValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

data class IntrospectRequest(
    val identity_provider: IdentityProvider,
    val token: String,
)

sealed class IntrospectResponse(val active: Boolean) {
    data class Valid(val claims: Map<String, Any>) : IntrospectResponse(active = true)

    data class Invalid(val error: String) : IntrospectResponse(active = false)
}

enum class IdentityProvider(
    @JsonValue
    val value: String,
) {
    ENTRA_ID("azuread"),
}

data class TokenRequest(
    val identity_provider: IdentityProvider,
    val target: String,
    val resource: String? = null,
    val skip_cache: Boolean = false,
)

data class TokenExchangeRequest(
    val identity_provider: IdentityProvider,
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

class TexasKlient(
    private val tokenEndpoint: String,
    private val tokenExchangeEndpoint: String,
    private val introspectEndpoint: String,
    private val httpClient: HttpClient = defaultHttpClient(),
) {
    suspend fun accessToken(
        target: String,
        identityProvider: IdentityProvider,
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
        identityProvider: IdentityProvider,
        skipCache: Boolean,
    ): TokenResponse {
        return kotlin.runCatching {
            httpClient.post(tokenExchangeEndpoint) {
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
        }.onFailure {
        }.getOrThrow()
    }

    suspend fun introspect(
        identityProvider: IdentityProvider,
        token: String,
    ): IntrospectResponse {
        return kotlin.runCatching {
            httpClient.post(introspectEndpoint) {
                header("Content-Type", "application/json")
                setBody(
                    IntrospectRequest(
                        identity_provider = identityProvider,
                        token = token,
                    ),
                )
            }.body<IntrospectResponse>()
        }.onFailure {
        }.getOrThrow()
    }
}
