package no.nav.dagpenger.oidc

interface OidcClient {
    fun oidcToken(): OidcToken
    suspend fun getOidcToken(): OidcToken
}
