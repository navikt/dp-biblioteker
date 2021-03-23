package no.nav.dagpenger.oidc

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import io.prometheus.client.Summary
import java.time.Duration
import java.time.LocalDateTime.now

/**
 * henter jwt token fra STS
 */

private val requestLatency = Summary.build()
    .quantile(0.5, 0.05) // Add 50th percentile (= median) with 5% tolerated error
    .quantile(0.9, 0.01) // Add 90th percentile with 1% tolerated error
    .quantile(0.99, 0.001) // Add 99th percentile with 0.1% tolerated error
    .name("oidc_requests_latency_seconds")
    .help("Request latency in seconds for Oidc client")
    .register()

class StsOidcClient(stsBaseUrl: String, private val username: String, private val password: String) : OidcClient {

    private val timeToRefresh: Long = 60
    private val stsTokenUrl: String =
        if (stsBaseUrl.endsWith("/")) "${stsBaseUrl}rest/v1/sts/token/" else "$stsBaseUrl/rest/v1/sts/token/"

    @Volatile
    private var tokenExpiryTime = now().minus(Duration.ofSeconds(timeToRefresh))

    @Volatile
    private lateinit var oidcToken: OidcToken

    override fun oidcToken(): OidcToken {
        val timer = requestLatency.startTimer()
        val token = if (now().isBefore(tokenExpiryTime)) {
            oidcToken
        } else {
            oidcToken = newOidcToken()
            tokenExpiryTime = now().plus(Duration.ofSeconds(oidcToken.expires_in - timeToRefresh))
            oidcToken
        }
        timer.observeDuration()
        return token
    }

    private fun newOidcToken(): OidcToken {
        val parameters = listOf(
            "grant_type" to "client_credentials",
            "scope" to "openid"
        )
        val (_, response, result) = with(stsTokenUrl.httpGet(parameters)) {
            authentication().basic(username, password)
            responseObject<OidcToken>()
        }
        when (result) {
            is Result.Failure -> throw StsOidcClientException(response.responseMessage, result.getException())
            is Result.Success -> return result.get()
        }
    }
}

class StsOidcClientException(override val message: String, override val cause: Throwable) :
    RuntimeException(message, cause)

data class OidcToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Long
)
