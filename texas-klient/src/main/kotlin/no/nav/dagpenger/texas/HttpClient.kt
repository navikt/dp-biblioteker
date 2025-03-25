package no.nav.dagpenger.texas

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson

fun defaultHttpClient(
    httpClientEngine: HttpClientEngine = CIO.create {},
    configure: List<HttpClientConfig<*>.() -> Unit> = listOf(defaultRetryConfig()),
) = HttpClient(httpClientEngine) {
    expectSuccess = true

    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    HttpResponseValidator {
        handleResponseException { cause, request ->
            when (cause) {
                is ClientRequestException -> {
                    val statusCode = cause.response.status
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> {
                            val errorResponse = cause.response.body<ErrorResponse>()
                            throw BadRequestException(statusCode, errorResponse)
                        }
                    }
                }

                is ServerResponseException -> {
                    val statusCode = cause.response.status
                    when (statusCode) {
                        HttpStatusCode.InternalServerError -> {
                            val errorResponse = cause.response.body<ErrorResponse>()
                            throw ServerError(statusCode, errorResponse)
                        }
                    }
                }
            }
        }
    }
    configure.forEach { it() }
}

fun defaultRetryConfig(): HttpClientConfig<*>.() -> Unit =
    {
        install(HttpRequestRetry) {
            retryOnException(maxRetries = 3, retryOnTimeout = true)
            exponentialDelay()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
            connectTimeoutMillis = 1000
            socketTimeoutMillis = 1000
        }
    }
