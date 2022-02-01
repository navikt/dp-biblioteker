package no.nav.dagpenger.client

import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Client
import no.nav.dagpenger.oauth2.OAuth2Config
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Manual test")
class ClientTest {

    @Test
    fun clientCredentialsTest() {
        val config = OAuth2Config.AzureAd(
            mapOf(
                OAuth2Config.AzureAd.clientIdKey to "",
                OAuth2Config.AzureAd.clientSecretKey to "",
                OAuth2Config.AzureAd.wellKnownUrlKey to "",
                OAuth2Config.AzureAd.privateJWKKey to """ """.trimIndent(),
                OAuth2Config.AzureAd.tokenEndpointUrlKey to "https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token"

            )
        )
        runBlocking {
            OAuth2Client(
                config.tokenEndpointUrl,
                config.clientSecret()
            ).clientCredentials("api://dev-fss.pdl.pdl-api/.default").also {
                println(it)
            }

            val cachedOauth2Client = CachedOauth2Client(
                tokenEndpointUrl = config.tokenEndpointUrl,
                authType = config.privateKey(),
            )

            (1..10).forEach {
                cachedOauth2Client.clientCredentials("api://dev-fss.pdl.pdl-api/.default").also {
                    println(it)
                }
            }
        }
    }
}
