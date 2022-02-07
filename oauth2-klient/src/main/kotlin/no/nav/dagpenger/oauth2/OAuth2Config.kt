package no.nav.dagpenger.oauth2

import com.fasterxml.jackson.annotation.JsonProperty
import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.Key
import com.natpryce.konfig.stringType
import com.nimbusds.jose.jwk.RSAKey
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import no.nav.security.token.support.client.core.jwk.JwkFactory

sealed class OAuth2Config {
    constructor(config: Configuration) {
        this.configuration = config
    }

    constructor(config: Map<String, String>) {
        this.configuration = ConfigurationMap(config)
    }

    abstract fun clientSecret(): AuthType.ClientSecret

    abstract fun privateKey(): AuthType.PrivateKey

    abstract fun wellKnowUrl(): String

    abstract val tokenEndpointUrl: String

    protected val configuration: Configuration
    protected fun fromJson(jsonKey: String): RSAKey = JwkFactory.fromJson(jsonKey)

    companion object {
        fun getTokenUrl(wellKnownUrl: String): String {
            return runBlocking {
                defaultHttpClient().get<WellKnown>(urlString = wellKnownUrl)
            }.tokenEndpointUrl
        }

        private data class WellKnown(
            @JsonProperty("token_endpoint")
            val tokenEndpointUrl: String
        )
    }

    class AzureAd : OAuth2Config {
        companion object {
            const val clientIdKey = "AZURE_APP_CLIENT_ID"
            const val clientSecretKey = "AZURE_APP_CLIENT_SECRET"
            const val privateJWKKey = "AZURE_APP_JWK"
            const val wellKnownUrlKey = "AZURE_APP_WELL_KNOWN_URL"
            const val tokenEndpointUrlKey = "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"
        }

        constructor(config: Configuration) : super(config)
        constructor(config: Map<String, String>) : super(config)

        override fun clientSecret(): AuthType.ClientSecret {
            return AuthType.ClientSecret(
                clientId = configuration[Key(clientIdKey, stringType)],
                clientSecret = configuration[Key(clientSecretKey, stringType)]
            )
        }

        override fun privateKey(): AuthType.PrivateKey {
            return AuthType.PrivateKey(
                clientId = configuration[Key(clientIdKey, stringType)],
                tokenEndpointUrl = tokenEndpointUrl,
                privateKey = fromJson(configuration[Key(privateJWKKey, stringType)])
            )
        }

        override fun wellKnowUrl(): String {
            return configuration[Key(wellKnownUrlKey, stringType)]
        }

        override val tokenEndpointUrl: String
            get() = configuration.getOrElse(Key(tokenEndpointUrlKey, stringType)) {
                getTokenUrl(wellKnownUrlKey)
            }
    }

    class TokenX : OAuth2Config {
        companion object {
            const val clientIdKey = "TOKEN_X_CLIENT_ID"
            const val privateJWKKey = "TOKEN_X_PRIVATE_JWK"
            const val wellKnownUrlKey = "TOKEN_X_WELL_KNOWN_URL"
        }

        constructor(config: Configuration) : super(config)

        constructor(config: Map<String, String>) : super(config)

        override val tokenEndpointUrl: String
            get() = getTokenUrl(wellKnowUrl())

        override fun clientSecret(): AuthType.ClientSecret {
            throw NotImplementedError("Not supported")
        }

        override fun privateKey(): AuthType.PrivateKey {
            return AuthType.PrivateKey(
                clientId = configuration[Key(clientIdKey, stringType)],
                tokenEndpointUrl = tokenEndpointUrl,
                privateKey = fromJson(configuration[Key(privateJWKKey, stringType)])
            )
        }

        override fun wellKnowUrl(): String {
            return configuration[Key(wellKnownUrlKey, stringType)]
        }
    }
}
