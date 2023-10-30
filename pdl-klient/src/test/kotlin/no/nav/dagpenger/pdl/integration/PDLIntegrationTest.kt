package no.nav.dagpenger.pdl.integration

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpHeaders
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Config
import no.nav.dagpenger.pdl.createPersonOppslagBolk
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.FileReader
import java.time.OffsetDateTime

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

fun getAzureAdToken(
    app: String,
    scope: String,
): String {
    val azureadConfig =
        OAuth2Config.AzureAd(
            getAuthEnv(app, "azurerator.nais.io"),
        )
    val tokenAzureAdClient: CachedOauth2Client by lazy {
        CachedOauth2Client(
            tokenEndpointUrl = azureadConfig.tokenEndpointUrl,
            authType = azureadConfig.clientSecret(),
        )
    }

//    val scope = "api://dev-gcp.teamdagpenger.dp-mellomlagring/.default"
    return tokenAzureAdClient.clientCredentials(scope).accessToken
}

@Disabled
class PDLIntegrationTest {
    @Test
    fun `kan hente personer fra pdl`() {
        runBlocking {
            val token = getAzureAdToken("dp-soknad", "api://dev-fss.pdl.pdl-api/.default")
            createPersonOppslagBolk("https://pdl-api.dev.intern.nav.no/graphql").hentPersoner(
                listOf("01038401226"),
                mapOf(HttpHeaders.Authorization to "Bearer $token"),
            ).size shouldBe 1
        }
    }
}
