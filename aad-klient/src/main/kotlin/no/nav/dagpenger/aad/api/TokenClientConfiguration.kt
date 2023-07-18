package no.nav.dagpenger.aad.api

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.Key
import com.natpryce.konfig.stringType

enum class GrantType(val type: String) {
    CLIENT_CREDENTIAL("client_credentials"),
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer"),
    TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange"),
}

class Authentication(configure: Authentication.() -> Unit) {
    var secret: String = ""

    init {
        this.apply(configure)
    }
}

class Cache(configure: Cache.() -> Unit = {}) {
    var maximumSize: Int = 10
    var skewInSeconds: Int = 5

    init {
        this.apply(configure)
    }
}

sealed class TokenClientConfiguration(env: Configuration) {
    var tokenEndPoint: String = env[Key("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", stringType)]
    internal abstract val grantType: GrantType
    var clientId: String = env[Key("AZURE_APP_CLIENT_ID", stringType)]
    var auth: Authentication =
        Authentication() { secret = env[Key("AZURE_APP_CLIENT_SECRET", stringType)] }
    internal var cache: Cache = Cache()
    fun cache(configure: Cache.() -> Unit = {}) {
        cache = Cache(configure)
    }

    private val defaultFormparameters: Map<String, String> by lazy {
        mapOf(
            "client_id" to clientId,
            "grant_type" to grantType.type,
            "client_secret" to auth.secret,
        )
    }
    protected abstract val clientFormparameters: Map<String, String>
    internal val formParameters by lazy { defaultFormparameters + clientFormparameters }

    open class ClientCredential(env: Configuration) : TokenClientConfiguration(env) {
        override val grantType = GrantType.CLIENT_CREDENTIAL
        private var scope: MutableList<String> = mutableListOf()
        fun scope(configure: MutableList<String>.() -> Unit) {
            scope = mutableListOf<String>().apply(configure)
        }

        override val clientFormparameters: Map<String, String>
            get() = mapOf("scope" to scope.joinToString(" "))
    }

    class Onbehalf(env: Configuration, override val grantType: GrantType = GrantType.JWT_BEARER) :
        ClientCredential(env) {

        override val clientFormparameters: Map<String, String>
            get() = super.clientFormparameters + mapOf("requested_token_use" to "on_behalf_of")
    }

    class TokenX(env: Configuration) : TokenClientConfiguration(env) {
        override val grantType = GrantType.TOKEN_EXCHANGE
        override val clientFormparameters: Map<String, String>
            get() = TODO("Not yet implemented")
        var audience: String? = null
        var resource: String? = null
    }
}
