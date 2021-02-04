package no.nav.dagpenger.oidc

interface OidcClient {
    suspend fun oidcToken(): OidcToken
}
