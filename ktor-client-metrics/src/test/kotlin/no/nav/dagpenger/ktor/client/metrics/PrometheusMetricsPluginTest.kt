package no.nav.dagpenger.ktor.client.metrics

import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.path
import io.prometheus.metrics.model.registry.PrometheusRegistry
import io.prometheus.metrics.model.snapshots.CounterSnapshot
import io.prometheus.metrics.model.snapshots.HistogramSnapshot
import io.prometheus.metrics.model.snapshots.MetricSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PrometheusMetricsPluginTest {
    private lateinit var defaultRegistry: PrometheusRegistry
    private lateinit var client: HttpClient

    @BeforeEach
    fun setUp() {
        defaultRegistry = PrometheusRegistry()
        client =
            HttpClient(MockEngine) {
                install(PrometheusMetricsPlugin) {
                    baseName = "basename"
                    this.registry = defaultRegistry
                }
                engine {
                    addHandler { request ->
                        when (request.url.encodedPath) {
                            "/measured" -> {
                                delay(100L)
                                respondOk("Hello, world")
                            }

                            "/ok" -> respondOk("Hello, world")
                            "/not-found" -> respondError(HttpStatusCode.NotFound)
                            else -> error("Unhandled URL ${request.url.encodedPath}")
                        }
                    }
                }
            }
    }

    private inline fun <reified T : MetricSnapshot> MetricSnapshot.getSnapShot(): T = this as T

    @Test
    fun `calls are timed`() {
        runBlocking {
            client.get { url { path("/measured") } }
        }
        defaultRegistry.scrape { it.contains("duration") }.single().getSnapShot<HistogramSnapshot>().let {
            it.dataPoints.single().let { data ->
                data.count shouldBe 1
                data.sum shouldBeGreaterThan 0.1
            }
        }
    }

    @Test
    fun `status codes are counted`() {
        runBlocking {
            try {
                client.get { url { path("/ok") } }
                client.get { url { path("/not-found") } }
            } catch (e: ClientRequestException) {
            }
        }

        defaultRegistry.scrape { it.contains("status") }.single().getSnapShot<CounterSnapshot>().let {
            it.getStatusValue("200") shouldBe 1.0
            it.getStatusValue("404") shouldBe 1.0
            it.getStatusValue("201") shouldBe null
        }
    }

    private fun CounterSnapshot.getStatusValue(status: String): Double? {
        return this.dataPoints.singleOrNull { it.labels["status"] == status }?.value
    }
}
