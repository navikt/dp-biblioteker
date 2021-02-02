package no.nav.dagpenger.oidc

import kotlin.time.ExperimentalTime

@ExperimentalTime
interface OidcClient {
    suspend fun oidcToken(): OidcToken
}
