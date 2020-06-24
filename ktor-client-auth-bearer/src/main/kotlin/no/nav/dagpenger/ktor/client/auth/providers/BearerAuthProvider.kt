package no.nav.dagpenger.ktor.client.auth.providers

import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.AuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader

fun Auth.bearer(block: BearerAuthConfig.() -> Unit) {
    with(BearerAuthConfig().apply(block)) {
        providers += BearerAuthProvider(
            tokenProvider,
            realm,
            sendWithoutRequest
        )
    }
}

data class BearerAuthConfig(
    var tokenProvider: () -> String = { "" },
    var realm: String? = null,
    var sendWithoutRequest: Boolean = false
)

class BearerAuthProvider(
    private val tokenProvider: () -> String,
    private val realm: String? = null,
    override val sendWithoutRequest: Boolean = false
) : AuthProvider {
    private val authScheme = "Bearer"

    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth.authScheme != authScheme) return false

        if (realm != null) {
            if (auth !is HttpAuthHeader.Parameterized) return false
            return auth.parameter("realm") == realm
        }

        return true
    }

    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
        request.headers[HttpHeaders.Authorization] = constructTokenAuthValue()
    }

    internal fun constructTokenAuthValue(): String = "Bearer ${tokenProvider()}"
}
