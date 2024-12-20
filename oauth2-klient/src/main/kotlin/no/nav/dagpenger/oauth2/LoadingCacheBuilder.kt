package no.nav.dagpenger.oauth2

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import kotlinx.coroutines.runBlocking
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import java.util.concurrent.TimeUnit

data class LoadingCacheBuilder(
    val maximumSize: Long = 1000,
    val evictSkew: Long = 5,
) {
    fun cache(loader: suspend (GrantRequest) -> OAuth2AccessTokenResponse): AsyncLoadingCache<GrantRequest, OAuth2AccessTokenResponse> =
        Caffeine
            .newBuilder()
            .expireAfter(evictOnResponseExpiresIn(evictSkew))
            .maximumSize(maximumSize)
            .buildAsync { key ->
                runBlocking { loader(key) }
            }

    private fun evictOnResponseExpiresIn(skewInSeconds: Long): Expiry<GrantRequest, OAuth2AccessTokenResponse> {
        return object : Expiry<GrantRequest, OAuth2AccessTokenResponse> {
            override fun expireAfterCreate(
                key: GrantRequest,
                response: OAuth2AccessTokenResponse,
                currentTime: Long,
            ): Long {
                if (response.expires_in == null) return 0

                val seconds =
                    if (response.expires_in!! > skewInSeconds) {
                        response.expires_in!! - skewInSeconds
                    } else {
                        response.expires_in!!.toLong()
                    }
                return TimeUnit.SECONDS.toNanos(seconds)
            }

            override fun expireAfterUpdate(
                key: GrantRequest,
                response: OAuth2AccessTokenResponse,
                currentTime: Long,
                currentDuration: Long,
            ): Long = currentDuration

            override fun expireAfterRead(
                key: GrantRequest,
                response: OAuth2AccessTokenResponse,
                currentTime: Long,
                currentDuration: Long,
            ): Long = currentDuration
        }
    }
}
