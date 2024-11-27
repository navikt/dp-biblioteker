package no.nav.dagpenger.client

import io.kotest.matchers.shouldNotBe
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Client
import no.nav.dagpenger.oauth2.OAuth2Config
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.FileReader
import java.time.OffsetDateTime
import kotlin.time.Duration.Companion.milliseconds

class ClientTest {
    // Hente azuread eller tokenx secret for  app
// jwker.nais.io -> tokenx,  azurerator.nais.io -> azuread
    fun getAuthEnv(
        app: String,
        type: String = "jwker.nais.io",
    ): Map<String, String> {
        // file path to your KubeConfig
        val kubeConfigPath = System.getenv("KUBECONFIG")

        // IF this fails do kubectl get pod to aquire credentials
        val client: ApiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(FileReader(kubeConfigPath))).build()
        Configuration.setDefaultApiClient(client)
        return CoreV1Api().listNamespacedSecret(
            "teamdagpenger",
            null,
            null,
            null,
            null,
            "app=$app,type=$type",
            null,
            null,
            null,
            null,
            null,
        ).items.also { secrets ->
            secrets.sortByDescending<V1Secret?, OffsetDateTime> { it?.metadata?.creationTimestamp }
        }.first<V1Secret?>()?.data!!.mapValues { e -> String(e.value) }
    }

    @Test
    @Disabled("Manual test")
    fun tokenxchange() {
        val config =
            OAuth2Config.TokenX(
                getAuthEnv("dp-soknad"),
            )

        val oAuth2Client =
            OAuth2Client(
                config.tokenEndpointUrl,
                config.privateKey(),
            )
        runBlocking {
            delay(5000.milliseconds) // Set client assertion to something less than this to recreate
            oAuth2Client.tokenExchange(
                token = "",
                audience = "dev-gcp:teamdagpenger:dp-innsyn",
            ).access_token.let {
                it shouldNotBe null
            }
        }
    }

    @Disabled("Manual test")
    @Test
    fun clientCredentialsTest() {
        val config =
            OAuth2Config.AzureAd(
                getAuthEnv("dp-soknad", "azurerator.nais.io"),
            )
        runBlocking {
            OAuth2Client(
                config.tokenEndpointUrl,
                config.clientSecret(),
            ).clientCredentials("api://dev-fss.pdl.pdl-api/.default").also {
                println(it)
            }

            val cachedOauth2Client =
                CachedOauth2Client(
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

    @Test
    @Disabled("Manual test")
    fun paralellTest() {
        "api://dev-gcp.nom.skjermede-personer-pip/.default"
        val config =
            OAuth2Config.AzureAd(
                getAuthEnv("dp-saksbehandling", "azurerator.nais.io"),
            )

        val cachedOauth2Client =
            CachedOauth2Client(tokenEndpointUrl = config.tokenEndpointUrl, authType = config.privateKey())

        runBlocking {
            val t1 =
                async {
                    cachedOauth2Client.clientCredentials("api://dev-gcp.nom.skjermede-personer-pip/.default")
                }
            val t2 =
                async {
                    cachedOauth2Client.clientCredentials("api://dev-fss.pdl.pdl-api/.default")
                }

            println("t1 = ${t1.await().access_token} t2 = ${t2.await().access_token}")
        }
    }
}
