package no.nav.dagpenger.ktor.client.metrics

import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.prometheus.client.Collector
import io.prometheus.client.CollectorRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApiKeyAuthTest {
    private val registry: CollectorRegistry = CollectorRegistry.defaultRegistry
    private lateinit var client: HttpClient

    @BeforeEach
    fun setUp() {
        client = HttpClient(MockEngine) {
            install(PrometheusMetrics) {
                baseName = ""
                this.registry = registry
            }
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "measured" -> {
                            delay(100L)
                            respondOk("Hello, world")
                        }
                        "ok" -> respondOk("Hello, world")
                        "not-found" -> respondError(HttpStatusCode.NotFound)
                        else -> error("Unhandled URL ${request.url.encodedPath}")
                    }
                }
            }
        }
    }

    @AfterEach
    fun tearDown() {
        registry.clear()
    }

    @Test
    fun `calls are timed`() {
        runBlocking {
            client.get<String>("/measured")
        }

        getCount("duration") shouldBe 1
        getSum("duration").shouldBeGreaterThan(0.1)
    }

    @Test
    fun `status codes are counted`() {
        runBlocking {
            try {
                client.get<String>("/ok")
                client.get<String>("/not-found")
            } catch (e: ClientRequestException) {
            }
        }

        getStatus("201") shouldBe null
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
}
