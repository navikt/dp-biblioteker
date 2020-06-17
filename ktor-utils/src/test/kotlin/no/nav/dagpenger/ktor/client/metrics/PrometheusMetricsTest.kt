package no.nav.dagpenger.ktor.client.metrics

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.feature
import io.ktor.client.request.get
import io.prometheus.client.Collector
import io.prometheus.client.CollectorRegistry
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiKeyAuthTest {
    private val wireMockServer = WireMockServer(
        WireMockConfiguration
            .options()
            .dynamicPort()
            .asynchronousResponseEnabled(false)
    )
    private val registry: CollectorRegistry = CollectorRegistry.defaultRegistry

    @BeforeAll
    fun setUp() {
        wireMockServer.start()
    }

    @AfterEach
    fun tearDown() {
        CollectorRegistry.defaultRegistry.clear()
    }

    @AfterAll
    fun cleanup() {
        wireMockServer.stop()
    }

    @Test
    fun `can be set up without custom config`() {
        HttpClient(CIO) {
            install(PrometheusMetrics)
        }.also {
            it.feature(PrometheusMetrics).shouldBeTypeOf<PrometheusMetrics>()
        }
    }

    @Test
    fun `calls are timed`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/measured"))
                .willReturn(
                    aResponse()
                        .withBody("it is!")
                        .withFixedDelay(100)
                )
        )

        HttpClient(CIO) {
            install(PrometheusMetrics) {
                baseName = ""
            }
        }.use {
            runBlocking {
                it.get<String>(withBaseUrl("measured"))
            }
        }

        getCount("duration") shouldBe 1
        getSum("duration").shouldBeGreaterThan(0.1)
    }

    @Test
    fun `status codes are counted`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/ok"))
                .willReturn(
                    aResponse()
                        .withBody("it is!")
                        .withStatus(200)
                )
        )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/not-found"))
                .willReturn(
                    aResponse()
                        .withBody("it is!")
                        .withStatus(404)
                )
        )

        HttpClient(CIO) {
            install(PrometheusMetrics) {
                baseName = ""
            }
        }.use {
            runBlocking {
                try {
                    it.get<String>(withBaseUrl("ok"))
                    it.get<String>(withBaseUrl("not-found"))
                } catch (e: Exception) {
                }
            }
        }

        getStatus("200") shouldBe 1
        getStatus("404") shouldBe 1
    }

    private fun getStatus(statusCode: String) =
        registry.getSampleValue("status", listOf("status").toTypedArray(), listOf(statusCode).toTypedArray())

    private fun getCount(name: String): Double = registry.getSampleValue("${name}_count").toDouble()
    private fun getSum(name: String): Double = registry.getSampleValue("${name}_sum").toDouble()
    private fun getBucket(name: String, bucket: Double): Double =
        registry.getSampleValue(
            "${name}_bucket",
            listOf("le").toTypedArray(),
            listOf(Collector.doubleToGoString(bucket)).toTypedArray()
        )

    private fun withBaseUrl(url: String) = "${wireMockServer.baseUrl()}/$url"
}
