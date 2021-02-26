package no.nav.dagpenger.aad.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import no.nav.dagpenger.aad.api.Cache
import no.nav.dagpenger.aad.http.AccessToken
import java.time.Duration
import java.time.temporal.ChronoUnit

internal fun <T> Cache.create(): com.github.benmanes.caffeine.cache.Cache<T, AccessToken> =
    Caffeine.newBuilder()
        .maximumSize(maximumSize.toLong())
        .expireAfter(
            object : Expiry<T, AccessToken> {
                override fun expireAfterCreate(key: T, value: AccessToken, currentTime: Long): Long =
                    Duration.of((value.expiresIn - skewInSeconds).toLong(), ChronoUnit.SECONDS).seconds

                override fun expireAfterUpdate(
                    key: T,
                    value: AccessToken,
                    currentTime: Long,
                    currentDuration: Long
                ): Long =
                    currentDuration

                override fun expireAfterRead(
                    key: T,
                    value: AccessToken,
                    currentTime: Long,
                    currentDuration: Long
                ): Long =
                    currentDuration
            }
        )
        .build()
