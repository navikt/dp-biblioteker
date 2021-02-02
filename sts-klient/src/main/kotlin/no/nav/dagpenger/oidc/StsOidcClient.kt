package no.nav.dagpenger.oidc

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.basic
import io.ktor.util.KtorExperimentalAPI
import io.prometheus.client.Summary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime

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

@KtorExperimentalAPI
@ExperimentalTime
class StsOidcClient(stsBaseUrl: String, private val username: String, private val password: String) : OidcClient {
    @KtorExperimentalAPI
    private val client = HttpClient(CIO) {
        install(Auth) {
            basic {
                username = this@StsOidcClient.username
                password = this@StsOidcClient.password
            }
        }
    }

    private var oidcToken: OidcToken? = null
    private val mutex = Mutex()

    private val stsTokenUrl: String =
        if (stsBaseUrl.endsWith("/")) "${stsBaseUrl}rest/v1/sts/token/" else "$stsBaseUrl/rest/v1/sts/token/"

    override suspend fun oidcToken(): OidcToken {
        mutex.withLock {
            val timer = requestLatency.startTimer()
            if (!OidcToken.isValid(oidcToken)) {
                oidcToken = newOidcToken()
            }
            timer.observeDuration()
            return oidcToken!!
        }
    }

    private suspend fun newOidcToken(): OidcToken {
        return withContext(Dispatchers.IO) {
        }
    }
}

class StsOidcClientException(override val message: String, override val cause: Throwable) :
    RuntimeException(message, cause)

@ExperimentalTime
data class OidcToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    private val timeToRefresh: Long = 60
) {
    val valid: Boolean
        get() = LocalDateTime.now() < expireTime
    val expireTime: LocalDateTime = LocalDateTime.now().plus(Duration.ofSeconds(this.expires_in - timeToRefresh))

    companion object {
        fun isValid(token: OidcToken?) = when (token) {
            null -> false
            else -> !token.valid
        }
    }
}
