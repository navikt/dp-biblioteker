package no.nav.dagpenger.ktor.client.metrics

import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter
import io.prometheus.client.Histogram

class PrometheusMetrics private constructor(registry: CollectorRegistry, baseName: String) {
    private val duration = Histogram
        .build("duration", "Time spent in requests")
        .namespace(baseName)
        .register(registry)

    private val httpStatus = Counter
        .build("status", "Count status codes for responses")
        .namespace(baseName)
        .labelNames("status")
        .register(registry)

    class Config {
        var baseName: String = "ktor_client_metrics"
        var registry: CollectorRegistry = CollectorRegistry.defaultRegistry
    }

    companion object Feature : HttpClientFeature<Config, PrometheusMetrics> {
        override val key = AttributeKey<PrometheusMetrics>("metrics")

        override fun prepare(block: Config.() -> Unit): PrometheusMetrics {
            val conf = Config().apply(block)
            return PrometheusMetrics(
                registry = conf.registry,
                baseName = conf.baseName
            )
        }

        override fun install(feature: PrometheusMetrics, scope: HttpClient) {
            val phase = PipelinePhase("PrometheusMetrics")

            scope.sendPipeline.insertPhaseBefore(HttpSendPipeline.Monitoring, phase)
            scope.sendPipeline.intercept(phase) {
                feature.before(this.context)
            }

            scope.receivePipeline.insertPhaseAfter(HttpReceivePipeline.After, phase)
            scope.receivePipeline.intercept(phase) {
                feature.after(this.context)
            }
        }
    }

    private data class CallMeasure(val timer: Histogram.Timer)

    private val metricKey = AttributeKey<CallMeasure>("metrics")

    private fun before(call: HttpRequestBuilder) {
        call.attributes.put(metricKey, CallMeasure(duration.startTimer()))
    }

    private fun after(call: HttpClientCall) {
        httpStatus
            .labels(call.response.status.value.toString())
            .inc()

        call.attributes.getOrNull(metricKey)?.apply {
            timer.observeDuration()
        }
    }
}
