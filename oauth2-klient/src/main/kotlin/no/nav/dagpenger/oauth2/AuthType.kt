package no.nav.dagpenger.oauth2

import com.nimbusds.jose.jwk.RSAKey
import no.nav.security.token.support.client.core.OAuth2ParameterNames
import no.nav.security.token.support.client.core.auth.ClientAssertion
import java.net.URI

sealed class AuthType {
    abstract val authParams: Map<String, String>

    data class ClientSecret(private val clientId: String, private val clientSecret: String) : AuthType() {
        override val authParams: Map<String, String> = mapOf(
            OAuth2ParameterNames.CLIENT_ID to clientId,
            OAuth2ParameterNames.CLIENT_SECRET to clientSecret,
        )
    }

    data class PrivateKey(
        private val clientId: String,
        private val tokenEndpointUrl: String,
        private val privateKey: RSAKey
    ) :
        AuthType() {
        private val clientAssertion = ClientAssertion(URI.create(tokenEndpointUrl), clientId, privateKey, 120)
        override val authParams: Map<String, String> = mapOf(
            OAuth2ParameterNames.CLIENT_ID to clientId,
            OAuth2ParameterNames.CLIENT_ASSERTION_TYPE to clientAssertion.assertionType(),
            OAuth2ParameterNames.CLIENT_ASSERTION to clientAssertion.assertion()
        )
    }
}
