package no.nav.dagpenger.oauth2

import no.nav.security.token.support.client.core.OAuth2GrantType
import no.nav.security.token.support.client.core.OAuth2GrantType.JWT_BEARER
import no.nav.security.token.support.client.core.OAuth2ParameterNames

sealed class GrantRequest(
    grantType: OAuth2GrantType,
    params: Map<String, String> = emptyMap(),
    authType: AuthType,
) {
    val formParams: Map<String, String> =
        mutableMapOf<String, String>(OAuth2ParameterNames.GRANT_TYPE to grantType.value) + params + authType.authParams

    data class TokenX(private val token: String, private val audience: String, private val authTypeParams: AuthType) :
        GrantRequest(
            OAuth2GrantType.TOKEN_EXCHANGE,
            mapOf(
                OAuth2ParameterNames.SUBJECT_TOKEN_TYPE to "urn:ietf:params:oauth:token-type:jwt",
                OAuth2ParameterNames.SUBJECT_TOKEN to token,
                OAuth2ParameterNames.AUDIENCE to audience,
            ),
            authTypeParams,
        )

    data class OnBeHalfOf(private val token: String, private val scope: String, private val authType: AuthType) :
        GrantRequest(
            grantType = JWT_BEARER,
            params = mapOf(
                OAuth2ParameterNames.SCOPE to scope,
                OAuth2ParameterNames.REQUESTED_TOKEN_USE to "on_behalf_of",
                OAuth2ParameterNames.ASSERTION to token,
            ),
            authType = authType,
        )

    data class ClientCredentials(private val scope: String, private val authType: AuthType) : GrantRequest(
        OAuth2GrantType.CLIENT_CREDENTIALS,
        mapOf(
            OAuth2ParameterNames.SCOPE to scope,
        ),
        authType,
    )
}
